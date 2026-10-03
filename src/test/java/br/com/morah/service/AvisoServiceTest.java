package br.com.morah.service;

import br.com.morah.config.ContextoUsuario;
import br.com.morah.dto.*;
import br.com.morah.exception.AvisoForaDeVigenciaException;
import br.com.morah.exception.NaoEncontradoException;
import br.com.morah.exception.RegraNegocioException;
import br.com.morah.mapper.AvisoMapper;
import br.com.morah.model.*;
import br.com.morah.model.enums.PrioridadeAviso;
import br.com.morah.model.enums.TipoPerfil;
import br.com.morah.model.enums.TipoPublicoAlvo;
import br.com.morah.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Teste de unidade do Service: sem Spring e sem banco. Os repositórios são "dublês" (mocks)
 * que respondem o que ensinarmos com when(...).thenReturn(...). Padrão de cada teste: Arrange / Act / Assert
 * (preparar, executar, verificar).
 */
@ExtendWith(MockitoExtension.class)
class AvisoServiceTest {

    @Mock ComunicadoRepository comunicadoRepository;
    @Mock LeituraComunicadoRepository leituraRepository;
    @Mock CondominioRepository condominioRepository;
    @Mock PessoaRepository pessoaRepository;
    @Mock BlocoRepository blocoRepository;
    @Mock UnidadeRepository unidadeRepository;
    @Spy AvisoMapper mapper = new AvisoMapper(); // mapper real (é só conversão, não precisa de dublê)
    @InjectMocks AvisoService service;

    ContextoUsuario sindico = new ContextoUsuario(1L, 10L, null, TipoPerfil.SINDICO);
    ContextoUsuario morador = new ContextoUsuario(1L, 20L, 5L, TipoPerfil.MORADOR);
    Condominio condominio;
    Pessoa autor;

    @BeforeEach
    void preparar() {
        condominio = new Condominio();
        condominio.setId(1L);
        autor = new Pessoa();
        autor.setId(10L);
        autor.setNome("Carlos Síndico");
    }

    private Comunicado avisoExistente(LocalDateTime expiraEm) {
        Comunicado c = new Comunicado();
        c.setId(99L);
        c.setCondominio(condominio);
        c.setAutor(autor);
        c.setTitulo("Titulo");
        c.setConteudo("Conteudo");
        c.setPrioridade(PrioridadeAviso.NORMAL);
        c.setPublicoAlvoTipo(TipoPublicoAlvo.CONDOMINIO);
        c.setPublicadoEm(LocalDateTime.now().minusHours(1));
        c.setExpiraEm(expiraEm);
        return c;
    }

    // ------------------------------------------------------------------ criar

    @Test
    void criar_sucesso_publicaParaTodoOCondominioComPrioridadePadrao() {
        when(condominioRepository.findById(1L)).thenReturn(Optional.of(condominio));
        when(pessoaRepository.findById(10L)).thenReturn(Optional.of(autor));
        when(comunicadoRepository.save(any(Comunicado.class))).thenAnswer(inv -> {
            Comunicado c = inv.getArgument(0);
            c.setId(1L); // simula o banco gerando o id
            return c;
        });
        var req = new AvisoCreateRequest("  Reunião  ", "Dia 10", null, "geral",
                new PublicoAlvoDto(TipoPublicoAlvo.CONDOMINIO, null, null), null);

        AvisoResponse resposta = service.criar(sindico, req);

        ArgumentCaptor<Comunicado> captor = ArgumentCaptor.forClass(Comunicado.class);
        verify(comunicadoRepository).save(captor.capture());
        assertThat(captor.getValue().getTitulo()).isEqualTo("Reunião");               // trim aplicado
        assertThat(captor.getValue().getPrioridade()).isEqualTo(PrioridadeAviso.NORMAL); // padrão do YAML
        assertThat(captor.getValue().getAutor()).isSameAs(autor);
        assertThat(resposta.id()).isEqualTo(1L);
        assertThat(resposta.lidoPeloUsuario()).isFalse();
        assertThat(resposta.autor().nome()).isEqualTo("Carlos Síndico");
    }

    @Test
    void criar_blocoDeOutroCondominio_lancaRegraNegocio422() {
        when(condominioRepository.findById(1L)).thenReturn(Optional.of(condominio));
        when(pessoaRepository.findById(10L)).thenReturn(Optional.of(autor));
        when(blocoRepository.findByIdAndCondominioId(77L, 1L)).thenReturn(Optional.empty());
        var req = new AvisoCreateRequest("T", "C", PrioridadeAviso.URGENTE, null,
                new PublicoAlvoDto(TipoPublicoAlvo.BLOCO, 77L, null), null);

        assertThatThrownBy(() -> service.criar(sindico, req))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Bloco 77");
        verify(comunicadoRepository, never()).save(any());
    }

    @Test
    void criar_tipoBlocoSemBlocoId_lancaRegraNegocio() {
        when(condominioRepository.findById(1L)).thenReturn(Optional.of(condominio));
        when(pessoaRepository.findById(10L)).thenReturn(Optional.of(autor));
        var req = new AvisoCreateRequest("T", "C", null, null, new PublicoAlvoDto(TipoPublicoAlvo.BLOCO, null, null), null);

        assertThatThrownBy(() -> service.criar(sindico, req)).isInstanceOf(RegraNegocioException.class);
    }

    // ------------------------------------------------------------------ buscar

    @Test
    void buscar_inexistente_lancaNaoEncontrado404() {
        when(comunicadoRepository.findByIdAndCondominioId(404L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(morador, 404L)).isInstanceOf(NaoEncontradoException.class);
    }

    @Test
    void buscar_foraDeVigencia_lancaAvisoForaDeVigencia410() {
        when(comunicadoRepository.findByIdAndCondominioId(99L, 1L))
                .thenReturn(Optional.of(avisoExistente(LocalDateTime.now().minusMinutes(1))));

        assertThatThrownBy(() -> service.buscar(morador, 99L)).isInstanceOf(AvisoForaDeVigenciaException.class);
        verify(leituraRepository, never()).save(any());
    }

    @Test
    void buscar_sucesso_registraALeituraNaPrimeiraVez() {
        when(comunicadoRepository.findByIdAndCondominioId(99L, 1L)).thenReturn(Optional.of(avisoExistente(null)));
        when(leituraRepository.existsByComunicadoIdAndPessoaId(99L, 20L)).thenReturn(false);
        when(pessoaRepository.getReferenceById(20L)).thenReturn(new Pessoa());

        AvisoResponse resposta = service.buscar(morador, 99L);

        verify(leituraRepository).save(any(LeituraComunicado.class));
        assertThat(resposta.lidoPeloUsuario()).isTrue();
    }

    @Test
    void buscar_avisoDeUnidadeDeOutroMorador_lancaNaoEncontrado() {
        Comunicado deOutraUnidade = avisoExistente(null);
        deOutraUnidade.setPublicoAlvoTipo(TipoPublicoAlvo.UNIDADE);
        Unidade outra = new Unidade();
        outra.setId(8L); // o morador do teste é da unidade 5
        deOutraUnidade.setUnidade(outra);
        when(comunicadoRepository.findByIdAndCondominioId(99L, 1L)).thenReturn(Optional.of(deOutraUnidade));

        assertThatThrownBy(() -> service.buscar(morador, 99L)).isInstanceOf(NaoEncontradoException.class);
    }

    // ------------------------------------------------------------------ atualizar / remover

    @Test
    void atualizar_alteraApenasOsCamposEnviados() {
        Comunicado existente = avisoExistente(null);
        when(comunicadoRepository.findByIdAndCondominioId(99L, 1L)).thenReturn(Optional.of(existente));

        service.atualizar(sindico, 99L, new AvisoUpdateRequest(null, null, PrioridadeAviso.URGENTE, null, null));

        assertThat(existente.getPrioridade()).isEqualTo(PrioridadeAviso.URGENTE);
        assertThat(existente.getTitulo()).isEqualTo("Titulo"); // não mudou
    }

    @Test
    void remover_expiraOAvisoAgora() {
        Comunicado existente = avisoExistente(null);
        when(comunicadoRepository.findByIdAndCondominioId(99L, 1L)).thenReturn(Optional.of(existente));

        service.remover(sindico, 99L);

        assertThat(existente.estaForaDeVigencia(LocalDateTime.now().plusSeconds(1))).isTrue();
    }

    @Test
    void remover_inexistente_lancaNaoEncontrado() {
        when(comunicadoRepository.findByIdAndCondominioId(404L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remover(sindico, 404L)).isInstanceOf(NaoEncontradoException.class);
    }
}
