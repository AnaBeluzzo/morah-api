package br.com.morah.service;

import br.com.morah.config.ContextoUsuario;
import br.com.morah.dto.*;
import br.com.morah.exception.AvisoForaDeVigenciaException;
import br.com.morah.exception.NaoEncontradoException;
import br.com.morah.exception.RegraNegocioException;
import br.com.morah.mapper.AvisoMapper;
import br.com.morah.model.*;
import br.com.morah.model.enums.PrioridadeAviso;
import br.com.morah.model.enums.TipoPublicoAlvo;
import br.com.morah.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * S de "Service": aqui moram as REGRAS DE NEGÓCIO. O controller só recebe/devolve HTTP;
 * o repository só fala com o banco; o service decide o que é permitido e como.
 *
 * @Transactional: cada método roda dentro de uma transação (tudo ou nada). Se uma exceção for lançada,
 * o banco desfaz as alterações. readOnly=true nas consultas é uma otimização.
 * @RequiredArgsConstructor (Lombok): gera o construtor com os campos "final" -> injeção de dependência.
 *
 * A checagem de PERFIL (só síndico publica) é feita antes, pelo PerfisInterceptor (@PerfisPermitidos).
 * Aqui tratamos regras de dados: isolamento por condomínio, público-alvo, vigência.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvisoService {

    private final ComunicadoRepository comunicadoRepository;
    private final LeituraComunicadoRepository leituraRepository;
    private final CondominioRepository condominioRepository;
    private final PessoaRepository pessoaRepository;
    private final BlocoRepository blocoRepository;
    private final UnidadeRepository unidadeRepository;
    private final AvisoMapper mapper;

    /** GET /avisos: avisos VIGENTES visíveis ao contexto, com filtros opcionais e paginação. */
    public AvisoPage listar(ContextoUsuario ctx, String categoria, PrioridadeAviso prioridade, Pageable pageable) {
        Specification<Comunicado> filtro = Specification
                .where(ComunicadoSpecs.doCondominio(ctx.condominioId()))
                .and(ComunicadoSpecs.vigente(LocalDateTime.now()));

        if (categoria != null && !categoria.isBlank()) {
            filtro = filtro.and(ComunicadoSpecs.comCategoria(categoria.trim()));
        }
        if (prioridade != null) {
            filtro = filtro.and(ComunicadoSpecs.comPrioridade(prioridade));
        }
        if (!ctx.veTodosOsAvisos()) { // morador/proprietário: só o que é destinado a eles
            filtro = filtro.and(ComunicadoSpecs.destinadoA(blocoDoUsuario(ctx), ctx.unidadeId()));
        }

        Page<Comunicado> pagina = comunicadoRepository.findAll(filtro, pageable);

        // Uma única consulta descobre quais avisos desta página o usuário já leu.
        Set<Long> ids = Set.copyOf(pagina.getContent().stream().map(Comunicado::getId).toList());
        Set<Long> lidos = ids.isEmpty() ? Set.of() : leituraRepository.findComunicadoIdsLidos(ctx.pessoaId(), ids);

        return new AvisoPage(
                pagina.getContent().stream().map(c -> mapper.paraResposta(c, lidos.contains(c.getId()))).toList(),
                PageMetadata.de(pagina));
    }

    /** POST /avisos: publica um novo aviso. */
    @Transactional
    public AvisoResponse criar(ContextoUsuario ctx, AvisoCreateRequest req) {
        Condominio condominio = condominioRepository.findById(ctx.condominioId())
                .orElseThrow(() -> new NaoEncontradoException("Condomínio não encontrado."));
        Pessoa autor = pessoaRepository.findById(ctx.pessoaId())
                .orElseThrow(() -> new NaoEncontradoException("Pessoa não encontrada."));

        Comunicado aviso = new Comunicado();
        aviso.setCondominio(condominio);
        aviso.setAutor(autor);
        aviso.setTitulo(req.titulo().trim());
        aviso.setConteudo(req.conteudo().trim());
        aviso.setPrioridade(req.prioridade() != null ? req.prioridade() : PrioridadeAviso.NORMAL); // padrão do YAML
        aviso.setCategoria(req.categoria());
        aviso.setExpiraEm(req.expiraEm());
        aviso.setPublicadoEm(LocalDateTime.now());
        definirPublicoAlvo(aviso, req.publicoAlvo(), ctx.condominioId());

        // TODO (etapa Notificações): enviar push aos destinatários (UC31, passo 4).
        Comunicado salvo = comunicadoRepository.save(aviso);
        return mapper.paraResposta(salvo, false);
    }

    /**
     * GET /avisos/{id}: detalha o aviso e REGISTRA A LEITURA do usuário.
     * 404 se não existe / é de outro condomínio / não é destinado a este usuário; 410 se expirou.
     */
    @Transactional
    public AvisoResponse buscar(ContextoUsuario ctx, Long id) {
        Comunicado aviso = comunicadoRepository.findByIdAndCondominioId(id, ctx.condominioId())
                .filter(a -> podeVer(ctx, a))
                .orElseThrow(() -> new NaoEncontradoException("Aviso " + id + " não encontrado."));

        if (aviso.estaForaDeVigencia(LocalDateTime.now())) {
            throw new AvisoForaDeVigenciaException("O aviso " + id + " não está mais em vigência.");
        }

        if (!leituraRepository.existsByComunicadoIdAndPessoaId(id, ctx.pessoaId())) {
            LeituraComunicado leitura = new LeituraComunicado();
            leitura.setComunicado(aviso);
            leitura.setPessoa(pessoaRepository.getReferenceById(ctx.pessoaId()));
            leitura.setLidoEm(LocalDateTime.now());
            leituraRepository.save(leitura);
        }
        return mapper.paraResposta(aviso, true);
    }

    /** PATCH /avisos/{id}: altera só os campos enviados. */
    @Transactional
    public AvisoResponse atualizar(ContextoUsuario ctx, Long id, AvisoUpdateRequest req) {
        Comunicado aviso = buscarDoCondominio(ctx, id);

        if (req.titulo() != null) aviso.setTitulo(req.titulo().trim());
        if (req.conteudo() != null) aviso.setConteudo(req.conteudo().trim());
        if (req.prioridade() != null) aviso.setPrioridade(req.prioridade());
        if (req.categoria() != null) aviso.setCategoria(req.categoria());
        if (req.expiraEm() != null) aviso.setExpiraEm(req.expiraEm());

        // Não precisa chamar save(): a entidade já está "gerenciada" e o JPA grava as mudanças ao fim da transação.
        boolean lido = leituraRepository.existsByComunicadoIdAndPessoaId(id, ctx.pessoaId());
        return mapper.paraResposta(aviso, lido);
    }

    /**
     * DELETE /avisos/{id}: "remover/expirar" (palavras do YAML). Fazemos exclusão LÓGICA:
     * o aviso é expirado agora, some do mural e passa a responder 410. O histórico fica no banco.
     */
    @Transactional
    public void remover(ContextoUsuario ctx, Long id) {
        Comunicado aviso = buscarDoCondominio(ctx, id);
        aviso.setExpiraEm(LocalDateTime.now());
    }

    // ---------------------------------------------------------------- auxiliares

    private Comunicado buscarDoCondominio(ContextoUsuario ctx, Long id) {
        return comunicadoRepository.findByIdAndCondominioId(id, ctx.condominioId())
                .orElseThrow(() -> new NaoEncontradoException("Aviso " + id + " não encontrado."));
    }

    /** Valida o público-alvo e confere que bloco/unidade pertencem ao MESMO condomínio do síndico. */
    private void definirPublicoAlvo(Comunicado aviso, PublicoAlvoDto alvo, Long condominioId) {
        aviso.setPublicoAlvoTipo(alvo.tipo());
        switch (alvo.tipo()) {
            case CONDOMINIO -> { /* sem bloco nem unidade */ }
            case BLOCO -> {
                if (alvo.blocoId() == null) {
                    throw new RegraNegocioException("publicoAlvo.blocoId é obrigatório quando tipo=bloco.");
                }
                aviso.setBloco(blocoRepository.findByIdAndCondominioId(alvo.blocoId(), condominioId)
                        .orElseThrow(() -> new RegraNegocioException("Bloco " + alvo.blocoId() + " não existe neste condomínio.")));
            }
            case UNIDADE -> {
                if (alvo.unidadeId() == null) {
                    throw new RegraNegocioException("publicoAlvo.unidadeId é obrigatório quando tipo=unidade.");
                }
                aviso.setUnidade(unidadeRepository.findByIdAndBlocoCondominioId(alvo.unidadeId(), condominioId)
                        .orElseThrow(() -> new RegraNegocioException("Unidade " + alvo.unidadeId() + " não existe neste condomínio.")));
            }
        }
    }

    /** Mesma regra de visibilidade da listagem, mas aplicada em memória a um único aviso. */
    private boolean podeVer(ContextoUsuario ctx, Comunicado aviso) {
        if (ctx.veTodosOsAvisos() || aviso.getPublicoAlvoTipo() == TipoPublicoAlvo.CONDOMINIO) {
            return true;
        }
        if (aviso.getPublicoAlvoTipo() == TipoPublicoAlvo.BLOCO) {
            Long blocoUsuario = blocoDoUsuario(ctx);
            return blocoUsuario != null && blocoUsuario.equals(aviso.getBloco().getId());
        }
        return ctx.unidadeId() != null && ctx.unidadeId().equals(aviso.getUnidade().getId());
    }

    /** Bloco da unidade do usuário (null se ele não tem unidade, ex.: portaria). */
    private Long blocoDoUsuario(ContextoUsuario ctx) {
        if (ctx.unidadeId() == null) {
            return null;
        }
        return unidadeRepository.findById(ctx.unidadeId()).map(u -> u.getBloco().getId()).orElse(null);
    }
}
