package br.com.morah.controller;

import br.com.morah.config.ContextoUsuario;
import br.com.morah.dto.*;
import br.com.morah.exception.AvisoForaDeVigenciaException;
import br.com.morah.exception.NaoEncontradoException;
import br.com.morah.model.enums.PrioridadeAviso;
import br.com.morah.model.enums.TipoPublicoAlvo;
import br.com.morah.service.AvisoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de "fatia web": sobe só a camada MVC (controllers, validação, interceptor, handler de erros)
 * e usa MockMvc para simular requisições HTTP sem abrir porta. O Service é um dublê (@MockitoBean),
 * então aqui testamos o contrato HTTP: status, JSON e permissões.
 * (MockMvc não aplica o context-path "/v1", por isso as URLs começam em /avisos.)
 */
@WebMvcTest(AvisoController.class)
class AvisoControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean AvisoService avisoService;

    private MockHttpServletRequestBuilder como(MockHttpServletRequestBuilder req, String perfil) {
        return req.header(ContextoUsuario.H_CONDOMINIO, "1")
                .header(ContextoUsuario.H_PESSOA, "10")
                .header(ContextoUsuario.H_UNIDADE, "5")
                .header(ContextoUsuario.H_PERFIL, perfil);
    }

    private AvisoResponse aviso() {
        return new AvisoResponse(1L, "Reunião", "Dia 10", PrioridadeAviso.URGENTE, "geral",
                new PublicoAlvoDto(TipoPublicoAlvo.CONDOMINIO, null, null),
                new PessoaResumo(10L, "Carlos", "4199"), LocalDateTime.now(), null, false);
    }

    private static final String JSON_VALIDO = """
            {"titulo":"Reunião","conteudo":"Dia 10","prioridade":"urgente","publicoAlvo":{"tipo":"condominio"}}""";

    // ------------------------------------------------------------------ sucesso

    @Test
    void listar_comoMorador_retorna200ComPagina() throws Exception {
        when(avisoService.listar(any(), eq("geral"), eq(PrioridadeAviso.URGENTE), any()))
                .thenReturn(new AvisoPage(List.of(aviso()), new PageMetadata(0, 20, 1, 1)));

        mvc.perform(como(get("/avisos?categoria=geral&prioridade=urgente"), "morador"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titulo").value("Reunião"))
                .andExpect(jsonPath("$.content[0].prioridade").value("urgente")) // minúsculo, como no YAML
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void criar_comoSindico_retorna201ComLocation() throws Exception {
        when(avisoService.criar(any(), any(AvisoCreateRequest.class))).thenReturn(aviso());

        mvc.perform(como(post("/avisos"), "sindico").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/avisos/1")))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void remover_comoSindico_retorna204() throws Exception {
        mvc.perform(como(delete("/avisos/1"), "sindico")).andExpect(status().isNoContent());
        verify(avisoService).remover(any(), eq(1L));
    }

    // ------------------------------------------------------------------ 404 / 410

    @Test
    void buscar_inexistente_retorna404ProblemDetail() throws Exception {
        when(avisoService.buscar(any(), eq(99L))).thenThrow(new NaoEncontradoException("Aviso 99 não encontrado."));

        mvc.perform(como(get("/avisos/99"), "morador"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Aviso 99 não encontrado."));
    }

    @Test
    void buscar_foraDeVigencia_retorna410() throws Exception {
        when(avisoService.buscar(any(), eq(5L))).thenThrow(new AvisoForaDeVigenciaException("expirado"));

        mvc.perform(como(get("/avisos/5"), "morador")).andExpect(status().isGone());
    }

    @Test
    void atualizar_inexistente_retorna404() throws Exception {
        when(avisoService.atualizar(any(), eq(99L), any())).thenThrow(new NaoEncontradoException("x"));

        mvc.perform(como(patch("/avisos/99"), "sindico").contentType(MediaType.APPLICATION_JSON).content("{\"titulo\":\"Novo\"}"))
                .andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------ 403 / 401 / 400

    @Test
    void criar_comoMorador_retorna403ESemChamarOService() throws Exception {
        mvc.perform(como(post("/avisos"), "morador").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
        verifyNoInteractions(avisoService);
    }

    @Test
    void remover_comoPortaria_retorna403() throws Exception {
        mvc.perform(como(delete("/avisos/1"), "portaria")).andExpect(status().isForbidden());
        verifyNoInteractions(avisoService);
    }

    @Test
    void listar_semHeaders_retorna401() throws Exception {
        mvc.perform(get("/avisos")).andExpect(status().isUnauthorized());
    }

    @Test
    void criar_semTitulo_retorna400ComListaDeErros() throws Exception {
        String semTitulo = "{\"conteudo\":\"x\",\"publicoAlvo\":{\"tipo\":\"condominio\"}}";

        mvc.perform(como(post("/avisos"), "sindico").contentType(MediaType.APPLICATION_JSON).content(semTitulo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("titulo"));
        verifyNoInteractions(avisoService);
    }
}
