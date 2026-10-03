package br.com.morah.service;

import br.com.morah.config.ContextoUsuario;
import br.com.morah.dto.*;
import br.com.morah.exception.ConflitoException;
import br.com.morah.exception.NaoEncontradoException;
import br.com.morah.exception.RegraNegocioException;
import br.com.morah.mapper.PortariaMapper;
import br.com.morah.model.*;
import br.com.morah.model.enums.DecisaoVisita;
import br.com.morah.model.enums.StatusAutorizacaoVisita;
import br.com.morah.model.enums.TipoAcesso;
import br.com.morah.model.enums.TipoNotificacao;
import br.com.morah.model.enums.TipoPerfil;
import br.com.morah.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Regras de negócio da Portaria: autorização de visitantes e registro de acessos.
 *
 * Ciclo de vida de uma autorização:
 *   PENDENTE --morador autoriza--> AUTORIZADA --porteiro registra ENTRADA--> (visitante dentro) --SAIDA--> fim
 *   PENDENTE --morador recusa---> RECUSADA
 *   PENDENTE --passou o prazo---> EXPIRADA (calculada na leitura; não é gravada)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortariaService {

    /** Prazo que o morador tem para responder. Vale depois virar propriedade do application.yml. */
    public static final Duration VALIDADE_AUTORIZACAO = Duration.ofMinutes(15);

    static final String STATUS_DENTRO = "dentro";
    static final String STATUS_FINALIZADO = "finalizado";

    private final AutorizacaoVisitaRepository autorizacaoRepository;
    private final AcessoRepository acessoRepository;
    private final VisitanteRepository visitanteRepository;
    private final UnidadeRepository unidadeRepository;
    private final VinculoUnidadeRepository vinculoRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final CondominioRepository condominioRepository;
    private final PessoaRepository pessoaRepository;
    private final PortariaMapper mapper;

    // ------------------------------------------------------------ autorizações

    /** POST /portaria/visitantes: registra o visitante, cria a autorização PENDENTE e avisa os moradores. */
    @Transactional
    public AutorizacaoVisitaResponse registrarVisitante(ContextoUsuario ctx, AutorizacaoVisitaCreateRequest req) {
        Unidade unidade = unidadeRepository.findByIdAndBlocoCondominioId(req.unidadeId(), ctx.condominioId())
                .orElseThrow(() -> new RegraNegocioException("Unidade " + req.unidadeId() + " não existe neste condomínio."));

        AutorizacaoVisita autorizacao = new AutorizacaoVisita();
        autorizacao.setVisitante(obterOuCriarVisitante(req.visitante()));
        autorizacao.setUnidade(unidade);
        autorizacao.setMotivo(req.motivo().trim());
        autorizacao.setFotoUrl(req.fotoUrl());
        autorizacao.setStatus(StatusAutorizacaoVisita.PENDENTE);
        autorizacao.setSolicitadoEm(LocalDateTime.now());
        autorizacaoRepository.save(autorizacao);

        notificarMoradores(autorizacao);
        return paraResposta(autorizacao);
    }

    /** GET /portaria/autorizacoes: portaria vê o condomínio todo; morador/proprietário só a própria unidade. */
    public AutorizacaoVisitaPage listarAutorizacoes(ContextoUsuario ctx, StatusAutorizacaoVisita status, Pageable pageable) {
        Specification<AutorizacaoVisita> filtro = Specification.where(AutorizacaoVisitaSpecs.doCondominio(ctx.condominioId()));
        if (ctx.perfil() != TipoPerfil.PORTARIA) {
            if (ctx.unidadeId() == null) {
                throw new RegraNegocioException("O contexto do usuário não possui unidade.");
            }
            filtro = filtro.and(AutorizacaoVisitaSpecs.daUnidade(ctx.unidadeId()));
        }
        if (status != null) {
            filtro = filtro.and(AutorizacaoVisitaSpecs.comStatus(status, LocalDateTime.now().minus(VALIDADE_AUTORIZACAO)));
        }
        Page<AutorizacaoVisita> pagina = autorizacaoRepository.findAll(filtro, pageable);
        return new AutorizacaoVisitaPage(pagina.getContent().stream().map(this::paraResposta).toList(), PageMetadata.de(pagina));
    }

    /** GET /portaria/autorizacoes/{id}. */
    public AutorizacaoVisitaResponse buscarAutorizacao(ContextoUsuario ctx, Long id) {
        return paraResposta(carregarVisivel(ctx, id));
    }

    /** PATCH .../decisao: o morador autoriza ou recusa. 409 se já decidida ou expirada. */
    @Transactional
    public AutorizacaoVisitaResponse decidir(ContextoUsuario ctx, Long id, DecisaoAutorizacaoRequest req) {
        AutorizacaoVisita autorizacao = carregarVisivel(ctx, id);

        StatusAutorizacaoVisita atual = statusEfetivo(autorizacao);
        if (atual == StatusAutorizacaoVisita.EXPIRADA) {
            throw new ConflitoException("A autorização " + id + " expirou.");
        }
        if (atual != StatusAutorizacaoVisita.PENDENTE) {
            throw new ConflitoException("A autorização " + id + " já foi decidida.");
        }

        autorizacao.setStatus(req.decisao() == DecisaoVisita.AUTORIZADO
                ? StatusAutorizacaoVisita.AUTORIZADA : StatusAutorizacaoVisita.RECUSADA);
        autorizacao.setRespondidoEm(LocalDateTime.now());
        autorizacao.setPessoa(pessoaRepository.getReferenceById(ctx.pessoaId()));
        return paraResposta(autorizacao);
    }

    /** POST .../reenviar: nova notificação aos moradores (só enquanto PENDENTE). */
    @Transactional
    public void reenviar(ContextoUsuario ctx, Long id) {
        AutorizacaoVisita autorizacao = carregarVisivel(ctx, id);
        if (statusEfetivo(autorizacao) != StatusAutorizacaoVisita.PENDENTE) {
            throw new ConflitoException("Só é possível reenviar autorizações pendentes.");
        }
        notificarMoradores(autorizacao);
    }

    // ------------------------------------------------------------ acessos

    /**
     * POST /portaria/acessos.
     * ENTRADA: exige autorização AUTORIZADA do mesmo visitante e que ele não esteja já dentro.
     * SAIDA: fecha o acesso aberto do visitante (grava a data de saída); 422 se ele não está dentro.
     */
    @Transactional
    public AcessoResponse registrarAcesso(ContextoUsuario ctx, AcessoCreateRequest req) {
        Visitante visitante = visitanteRepository.findById(req.visitanteId())
                .orElseThrow(() -> new NaoEncontradoException("Visitante " + req.visitanteId() + " não encontrado."));
        var aberto = acessoRepository.findFirstByCondominioIdAndVisitanteIdAndSaidaIsNullOrderByEntradaDesc(
                ctx.condominioId(), visitante.getId());

        if (req.tipo() == TipoAcesso.SAIDA) {
            Acesso acesso = aberto.orElseThrow(() -> new RegraNegocioException("O visitante não está no condomínio."));
            acesso.setSaida(LocalDateTime.now());
            acesso.setTipo(TipoAcesso.SAIDA);
            acesso.setStatus(STATUS_FINALIZADO);
            return mapper.paraResposta(acesso);
        }

        if (aberto.isPresent()) {
            throw new ConflitoException("O visitante já está no condomínio.");
        }
        AutorizacaoVisita autorizacao = exigirAutorizacaoValida(ctx, req, visitante);

        Acesso acesso = new Acesso();
        acesso.setCondominio(condominioRepository.getReferenceById(ctx.condominioId()));
        acesso.setAutorizacao(autorizacao);
        acesso.setVisitante(visitante);
        acesso.setPessoa(pessoaRepository.getReferenceById(ctx.pessoaId())); // o porteiro
        acesso.setEntrada(LocalDateTime.now());
        acesso.setTipo(TipoAcesso.ENTRADA);
        acesso.setStatus(STATUS_DENTRO);
        acesso.setFotoUrl(req.fotoUrl());
        return mapper.paraResposta(acessoRepository.save(acesso));
    }

    /** GET /portaria/acessos?data=: histórico do condomínio, opcionalmente de um dia. */
    public AcessoPage listarAcessos(ContextoUsuario ctx, LocalDate data, Pageable pageable) {
        Page<Acesso> pagina = (data == null)
                ? acessoRepository.findByCondominioId(ctx.condominioId(), pageable)
                : acessoRepository.findByCondominioIdAndEntradaBetween(
                        ctx.condominioId(), data.atStartOfDay(), data.plusDays(1).atStartOfDay(), pageable);
        return new AcessoPage(pagina.getContent().stream().map(mapper::paraResposta).toList(), PageMetadata.de(pagina));
    }

    // ------------------------------------------------------------ auxiliares

    private AutorizacaoVisita exigirAutorizacaoValida(ContextoUsuario ctx, AcessoCreateRequest req, Visitante visitante) {
        if (req.autorizacaoId() == null) {
            throw new RegraNegocioException("autorizacaoId é obrigatório para registrar uma entrada.");
        }
        AutorizacaoVisita autorizacao = autorizacaoRepository
                .findByIdAndUnidadeBlocoCondominioId(req.autorizacaoId(), ctx.condominioId())
                .orElseThrow(() -> new RegraNegocioException("Autorização " + req.autorizacaoId() + " não existe neste condomínio."));
        if (!autorizacao.getVisitante().getId().equals(visitante.getId())) {
            throw new RegraNegocioException("A autorização não pertence a este visitante.");
        }
        if (autorizacao.getStatus() != StatusAutorizacaoVisita.AUTORIZADA) {
            throw new RegraNegocioException("A entrada só é permitida com autorização AUTORIZADA.");
        }
        return autorizacao;
    }

    /** Morador/proprietário só enxergam autorizações da própria unidade; as demais são 404 (não vazamos que existem). */
    private AutorizacaoVisita carregarVisivel(ContextoUsuario ctx, Long id) {
        AutorizacaoVisita autorizacao = autorizacaoRepository.findByIdAndUnidadeBlocoCondominioId(id, ctx.condominioId())
                .orElseThrow(() -> new NaoEncontradoException("Autorização " + id + " não encontrada."));
        boolean outraUnidade = ctx.perfil() != TipoPerfil.PORTARIA && !autorizacao.getUnidade().getId().equals(ctx.unidadeId());
        if (outraUnidade) {
            throw new NaoEncontradoException("Autorização " + id + " não encontrada.");
        }
        return autorizacao;
    }

    /** PENDENTE vencida conta como EXPIRADA. */
    StatusAutorizacaoVisita statusEfetivo(AutorizacaoVisita a) {
        boolean vencida = a.getStatus() == StatusAutorizacaoVisita.PENDENTE
                && a.getSolicitadoEm().plus(VALIDADE_AUTORIZACAO).isBefore(LocalDateTime.now());
        return vencida ? StatusAutorizacaoVisita.EXPIRADA : a.getStatus();
    }

    private AutorizacaoVisitaResponse paraResposta(AutorizacaoVisita a) {
        return mapper.paraResposta(a, statusEfetivo(a));
    }

    private Visitante obterOuCriarVisitante(VisitanteInput in) {
        String documento = (in.documento() == null || in.documento().isBlank()) ? null : in.documento().trim();
        if (documento != null) {
            var existente = visitanteRepository.findFirstByDocumento(documento);
            if (existente.isPresent()) {
                return existente.get();
            }
        }
        Visitante v = new Visitante();
        v.setNome(in.nome().trim());
        v.setDocumento(documento);
        v.setTelefone(in.telefone());
        return visitanteRepository.save(v);
    }

    /** Grava uma NOTIFICACAO para cada pessoa com vínculo vigente na unidade (o push de verdade fica para depois). */
    private void notificarMoradores(AutorizacaoVisita autorizacao) {
        List<Pessoa> moradores = vinculoRepository.findPessoasVigentesDaUnidade(autorizacao.getUnidade().getId(), LocalDate.now());
        for (Pessoa morador : moradores) {
            Notificacao n = new Notificacao();
            n.setPessoa(morador);
            n.setTipo(TipoNotificacao.PORTARIA);
            n.setIdReferencia(autorizacao.getId());
            n.setTitulo("Visitante na portaria");
            n.setMensagem(autorizacao.getVisitante().getNome() + " aguarda autorização de entrada.");
            n.setEnviadaEm(LocalDateTime.now());
            notificacaoRepository.save(n);
        }
    }
}
