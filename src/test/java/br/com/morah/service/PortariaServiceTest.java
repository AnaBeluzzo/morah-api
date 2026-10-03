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
import br.com.morah.model.enums.TipoPerfil;
import br.com.morah.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Teste de unidade do PortariaService (sem Spring, sem banco): repositórios são dublês. */
@ExtendWith(MockitoExtension.class)
class PortariaServiceTest {

    @Mock AutorizacaoVisitaRepository autorizacaoRepository;
    @Mock AcessoRepository acessoRepository;
    @Mock VisitanteRepository visitanteRepository;
    @Mock UnidadeRepository unidadeRepository;
    @Mock VinculoUnidadeRepository vinculoRepository;
    @Mock NotificacaoRepository notificacaoRepository;
    @Mock CondominioRepository condominioRepository;
    @Mock PessoaRepository pessoaRepository;
    @Spy PortariaMapper mapper = new PortariaMapper();
    @InjectMocks PortariaService service;

    ContextoUsuario portaria = new ContextoUsuario(1L, 40L, null, TipoPerfil.PORTARIA);
    ContextoUsuario morador = new ContextoUsuario(1L, 20L, 5L, TipoPerfil.MORADOR);
    Unidade unidade;
    Visitante visitante;

    @BeforeEach
    void preparar() {
        unidade = new Unidade();
        unidade.setId(5L);
        visitante = new Visitante();
        visitante.setId(7L);
        visitante.setNome("Ana Souza");
    }

    private AutorizacaoVisita autorizacao(StatusAutorizacaoVisita status, LocalDateTime solicitadoEm) {
        AutorizacaoVisita a = new AutorizacaoVisita();
        a.setId(50L);
        a.setUnidade(unidade);
        a.setVisitante(visitante);
        a.setMotivo("Entrega");
        a.setStatus(status);
        a.setSolicitadoEm(solicitadoEm);
        return a;
    }

    // ------------------------------------------------------------------ registrarVisitante

    @Test
    void registrarVisitante_sucesso_criaPendenteENotificaMoradores() {
        when(unidadeRepository.findByIdAndBlocoCondominioId(5L, 1L)).thenReturn(Optional.of(unidade));
        when(visitanteRepository.findFirstByDocumento("123")).thenReturn(Optional.empty());
        when(visitanteRepository.save(any(Visitante.class))).thenAnswer(inv -> {
            Visitante v = inv.getArgument(0);
            v.setId(7L);
            return v;
        });
        when(autorizacaoRepository.save(any(AutorizacaoVisita.class))).thenAnswer(inv -> {
            AutorizacaoVisita a = inv.getArgument(0);
            a.setId(50L);
            return a;
        });
        when(vinculoRepository.findPessoasVigentesDaUnidade(eq5(), any())).thenReturn(List.of(new Pessoa(), new Pessoa()));

        var req = new AutorizacaoVisitaCreateRequest(5L, new VisitanteInput(" Ana Souza ", "123", null), "Entrega", null);
        AutorizacaoVisitaResponse resp = service.registrarVisitante(portaria, req);

        assertThat(resp.status()).isEqualTo(StatusAutorizacaoVisita.PENDENTE);
        assertThat(resp.visitante().nome()).isEqualTo("Ana Souza");
        verify(notificacaoRepository, times(2)).save(any(Notificacao.class)); // um por morador
    }

    private static Long eq5() {
        return org.mockito.ArgumentMatchers.eq(5L);
    }

    @Test
    void registrarVisitante_unidadeDeOutroCondominio_lancaRegraNegocio() {
        when(unidadeRepository.findByIdAndBlocoCondominioId(99L, 1L)).thenReturn(Optional.empty());
        var req = new AutorizacaoVisitaCreateRequest(99L, new VisitanteInput("Ana", null, null), "x", null);

        assertThatThrownBy(() -> service.registrarVisitante(portaria, req)).isInstanceOf(RegraNegocioException.class);
        verify(autorizacaoRepository, never()).save(any());
    }

    // ------------------------------------------------------------------ decidir

    @Test
    void decidir_pendente_autoriza() {
        AutorizacaoVisita a = autorizacao(StatusAutorizacaoVisita.PENDENTE, LocalDateTime.now().minusMinutes(2));
        when(autorizacaoRepository.findByIdAndUnidadeBlocoCondominioId(50L, 1L)).thenReturn(Optional.of(a));
        when(pessoaRepository.getReferenceById(20L)).thenReturn(new Pessoa());

        AutorizacaoVisitaResponse resp = service.decidir(morador, 50L, new DecisaoAutorizacaoRequest(DecisaoVisita.AUTORIZADO, null));

        assertThat(resp.status()).isEqualTo(StatusAutorizacaoVisita.AUTORIZADA);
        assertThat(a.getRespondidoEm()).isNotNull();
    }

    @Test
    void decidir_jaDecidida_lancaConflito409() {
        AutorizacaoVisita a = autorizacao(StatusAutorizacaoVisita.RECUSADA, LocalDateTime.now().minusMinutes(2));
        when(autorizacaoRepository.findByIdAndUnidadeBlocoCondominioId(50L, 1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> service.decidir(morador, 50L, new DecisaoAutorizacaoRequest(DecisaoVisita.AUTORIZADO, null)))
                .isInstanceOf(ConflitoException.class);
    }

    @Test
    void decidir_pendenteVencida_lancaConflito409() {
        AutorizacaoVisita a = autorizacao(StatusAutorizacaoVisita.PENDENTE, LocalDateTime.now().minusMinutes(30));
        when(autorizacaoRepository.findByIdAndUnidadeBlocoCondominioId(50L, 1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> service.decidir(morador, 50L, new DecisaoAutorizacaoRequest(DecisaoVisita.AUTORIZADO, null)))
                .isInstanceOf(ConflitoException.class).hasMessageContaining("expirou");
        assertThat(a.getStatus()).isEqualTo(StatusAutorizacaoVisita.PENDENTE); // não foi alterada
    }

    @Test
    void decidir_autorizacaoDeOutraUnidade_lancaNaoEncontrado404() {
        unidade.setId(8L); // o morador do teste é da unidade 5
        AutorizacaoVisita a = autorizacao(StatusAutorizacaoVisita.PENDENTE, LocalDateTime.now());
        when(autorizacaoRepository.findByIdAndUnidadeBlocoCondominioId(50L, 1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> service.decidir(morador, 50L, new DecisaoAutorizacaoRequest(DecisaoVisita.RECUSADO, null)))
                .isInstanceOf(NaoEncontradoException.class);
    }

    @Test
    void buscarAutorizacao_vencida_retornaStatusExpirada() {
        AutorizacaoVisita a = autorizacao(StatusAutorizacaoVisita.PENDENTE, LocalDateTime.now().minusHours(1));
        when(autorizacaoRepository.findByIdAndUnidadeBlocoCondominioId(50L, 1L)).thenReturn(Optional.of(a));

        assertThat(service.buscarAutorizacao(portaria, 50L).status()).isEqualTo(StatusAutorizacaoVisita.EXPIRADA);
    }

    // ------------------------------------------------------------------ reenviar

    @Test
    void reenviar_pendente_notificaDeNovo() {
        AutorizacaoVisita a = autorizacao(StatusAutorizacaoVisita.PENDENTE, LocalDateTime.now());
        when(autorizacaoRepository.findByIdAndUnidadeBlocoCondominioId(50L, 1L)).thenReturn(Optional.of(a));
        when(vinculoRepository.findPessoasVigentesDaUnidade(any(), any())).thenReturn(List.of(new Pessoa()));

        service.reenviar(portaria, 50L);

        verify(notificacaoRepository).save(any(Notificacao.class));
    }

    @Test
    void reenviar_jaAutorizada_lancaConflito() {
        AutorizacaoVisita a = autorizacao(StatusAutorizacaoVisita.AUTORIZADA, LocalDateTime.now());
        when(autorizacaoRepository.findByIdAndUnidadeBlocoCondominioId(50L, 1L)).thenReturn(Optional.of(a));

        assertThatThrownBy(() -> service.reenviar(portaria, 50L)).isInstanceOf(ConflitoException.class);
    }

    // ------------------------------------------------------------------ registrarAcesso

    @Test
    void registrarAcesso_entradaComAutorizacaoAutorizada_criaAcessoDentro() {
        when(visitanteRepository.findById(7L)).thenReturn(Optional.of(visitante));
        when(acessoRepository.findFirstByCondominioIdAndVisitanteIdAndSaidaIsNullOrderByEntradaDesc(1L, 7L)).thenReturn(Optional.empty());
        when(autorizacaoRepository.findByIdAndUnidadeBlocoCondominioId(50L, 1L))
                .thenReturn(Optional.of(autorizacao(StatusAutorizacaoVisita.AUTORIZADA, LocalDateTime.now())));
        when(condominioRepository.getReferenceById(1L)).thenReturn(new Condominio());
        when(pessoaRepository.getReferenceById(40L)).thenReturn(new Pessoa());
        when(acessoRepository.save(any(Acesso.class))).thenAnswer(inv -> inv.getArgument(0));

        AcessoResponse resp = service.registrarAcesso(portaria, new AcessoCreateRequest(50L, 7L, TipoAcesso.ENTRADA, null));

        assertThat(resp.status()).isEqualTo("dentro");
        assertThat(resp.entrada()).isNotNull();
        assertThat(resp.saida()).isNull();
    }

    @Test
    void registrarAcesso_entradaComAutorizacaoPendente_lancaRegraNegocio() {
        when(visitanteRepository.findById(7L)).thenReturn(Optional.of(visitante));
        when(acessoRepository.findFirstByCondominioIdAndVisitanteIdAndSaidaIsNullOrderByEntradaDesc(1L, 7L)).thenReturn(Optional.empty());
        when(autorizacaoRepository.findByIdAndUnidadeBlocoCondominioId(50L, 1L))
                .thenReturn(Optional.of(autorizacao(StatusAutorizacaoVisita.PENDENTE, LocalDateTime.now())));

        assertThatThrownBy(() -> service.registrarAcesso(portaria, new AcessoCreateRequest(50L, 7L, TipoAcesso.ENTRADA, null)))
                .isInstanceOf(RegraNegocioException.class);
        verify(acessoRepository, never()).save(any());
    }

    @Test
    void registrarAcesso_entradaSemAutorizacaoId_lancaRegraNegocio() {
        when(visitanteRepository.findById(7L)).thenReturn(Optional.of(visitante));
        when(acessoRepository.findFirstByCondominioIdAndVisitanteIdAndSaidaIsNullOrderByEntradaDesc(1L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrarAcesso(portaria, new AcessoCreateRequest(null, 7L, TipoAcesso.ENTRADA, null)))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void registrarAcesso_entradaDeQuemJaEstaDentro_lancaConflito() {
        when(visitanteRepository.findById(7L)).thenReturn(Optional.of(visitante));
        when(acessoRepository.findFirstByCondominioIdAndVisitanteIdAndSaidaIsNullOrderByEntradaDesc(1L, 7L))
                .thenReturn(Optional.of(new Acesso()));

        assertThatThrownBy(() -> service.registrarAcesso(portaria, new AcessoCreateRequest(50L, 7L, TipoAcesso.ENTRADA, null)))
                .isInstanceOf(ConflitoException.class);
    }

    @Test
    void registrarAcesso_saida_fechaOAcessoAberto() {
        Acesso aberto = new Acesso();
        aberto.setVisitante(visitante);
        aberto.setEntrada(LocalDateTime.now().minusHours(1));
        aberto.setStatus("dentro");
        aberto.setTipo(TipoAcesso.ENTRADA);
        when(visitanteRepository.findById(7L)).thenReturn(Optional.of(visitante));
        when(acessoRepository.findFirstByCondominioIdAndVisitanteIdAndSaidaIsNullOrderByEntradaDesc(1L, 7L)).thenReturn(Optional.of(aberto));

        AcessoResponse resp = service.registrarAcesso(portaria, new AcessoCreateRequest(null, 7L, TipoAcesso.SAIDA, null));

        assertThat(resp.saida()).isNotNull();
        assertThat(resp.status()).isEqualTo("finalizado");
    }

    @Test
    void registrarAcesso_saidaDeQuemNaoEstaDentro_lancaRegraNegocio() {
        when(visitanteRepository.findById(7L)).thenReturn(Optional.of(visitante));
        when(acessoRepository.findFirstByCondominioIdAndVisitanteIdAndSaidaIsNullOrderByEntradaDesc(1L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrarAcesso(portaria, new AcessoCreateRequest(null, 7L, TipoAcesso.SAIDA, null)))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void registrarAcesso_visitanteInexistente_lancaNaoEncontrado() {
        when(visitanteRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrarAcesso(portaria, new AcessoCreateRequest(null, 404L, TipoAcesso.ENTRADA, null)))
                .isInstanceOf(NaoEncontradoException.class);
    }
}
