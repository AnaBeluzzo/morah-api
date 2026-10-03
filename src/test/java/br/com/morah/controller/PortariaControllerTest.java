package br.com.morah.controller;

import br.com.morah.config.ContextoUsuario;
import br.com.morah.dto.*;
import br.com.morah.exception.ConflitoException;
import br.com.morah.exception.NaoEncontradoException;
import br.com.morah.model.enums.StatusAutorizacaoVisita;
import br.com.morah.service.PortariaService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Teste de fatia web do PortariaController: contrato HTTP (status, JSON, permissões, validação). */
@WebMvcTest(PortariaController.class)
class PortariaControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean PortariaService portariaService;

    private MockHttpServletRequestBuilder como(MockHttpServletRequestBuilder req, String perfil) {
        return req.header(ContextoUsuario.H_CONDOMINIO, "1")
                .header(ContextoUsuario.H_PESSOA, "10")
                .header(ContextoUsuario.H_UNIDADE, "5")
                .header(ContextoUsuario.H_PERFIL, perfil);
    }

    private AutorizacaoVisitaResponse autorizacao() {
        return new AutorizacaoVisitaResponse(50L, new VisitanteResponse(7L, "Ana", "123", null, null), 5L,
                "Entrega", null, LocalDateTime.now(), null, StatusAutorizacaoVisita.PENDENTE);
    }

    private static final String VISITA_JSON = """
            {"unidadeId":5,"visitante":{"nome":"Ana","documento":"123"},"motivo":"Entrega"}""";

    @Test
    void registrarVisitante_comoPortaria_retorna201() throws Exception {
        when(portariaService.registrarVisitante(any(), any(AutorizacaoVisitaCreateRequest.class))).thenReturn(autorizacao());

        mvc.perform(como(post("/portaria/visitantes"), "portaria").contentType(MediaType.APPLICATION_JSON).content(VISITA_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/portaria/autorizacoes/50")))
                .andExpect(jsonPath("$.status").value("pendente"))
                .andExpect(jsonPath("$.visitante.nome").value("Ana"));
    }

    @Test
    void registrarVisitante_comoMorador_retorna403() throws Exception {
        mvc.perform(como(post("/portaria/visitantes"), "morador").contentType(MediaType.APPLICATION_JSON).content(VISITA_JSON))
                .andExpect(status().isForbidden());
        verifyNoInteractions(portariaService);
    }

    @Test
    void registrarVisitante_semMotivo_retorna400() throws Exception {
        String semMotivo = "{\"unidadeId\":5,\"visitante\":{\"nome\":\"Ana\"}}";

        mvc.perform(como(post("/portaria/visitantes"), "portaria").contentType(MediaType.APPLICATION_JSON).content(semMotivo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("motivo"));
    }

    @Test
    void registrarVisitante_visitanteSemNome_retorna400PorValidacaoAninhada() throws Exception {
        String semNome = "{\"unidadeId\":5,\"visitante\":{\"documento\":\"1\"},\"motivo\":\"x\"}";

        mvc.perform(como(post("/portaria/visitantes"), "portaria").contentType(MediaType.APPLICATION_JSON).content(semNome))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("visitante.nome"));
    }

    @Test
    void listarAutorizacoes_comFiltroDeStatus_retorna200() throws Exception {
        when(portariaService.listarAutorizacoes(any(), eq(StatusAutorizacaoVisita.PENDENTE), any()))
                .thenReturn(new AutorizacaoVisitaPage(List.of(autorizacao()), new PageMetadata(0, 20, 1, 1)));

        mvc.perform(como(get("/portaria/autorizacoes?status=pendente"), "morador"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(50))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void listarAutorizacoes_comoSindico_retorna403() throws Exception {
        mvc.perform(como(get("/portaria/autorizacoes"), "sindico")).andExpect(status().isForbidden());
    }

    @Test
    void buscarAutorizacao_inexistente_retorna404() throws Exception {
        when(portariaService.buscarAutorizacao(any(), eq(99L))).thenThrow(new NaoEncontradoException("nao existe"));

        mvc.perform(como(get("/portaria/autorizacoes/99"), "portaria"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void decidir_comoMorador_retorna200() throws Exception {
        when(portariaService.decidir(any(), eq(50L), any())).thenReturn(autorizacao());

        mvc.perform(como(patch("/portaria/autorizacoes/50/decisao"), "morador")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"decisao\":\"autorizado\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void decidir_comoPortaria_retorna403() throws Exception {
        mvc.perform(como(patch("/portaria/autorizacoes/50/decisao"), "portaria")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"decisao\":\"autorizado\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void decidir_jaDecidida_retorna409() throws Exception {
        when(portariaService.decidir(any(), eq(50L), any())).thenThrow(new ConflitoException("ja decidida"));

        mvc.perform(como(patch("/portaria/autorizacoes/50/decisao"), "proprietario")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"decisao\":\"recusado\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void decidir_decisaoInvalida_retorna400() throws Exception {
        mvc.perform(como(patch("/portaria/autorizacoes/50/decisao"), "morador")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"decisao\":\"talvez\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reenviar_comoPortaria_retorna202() throws Exception {
        mvc.perform(como(post("/portaria/autorizacoes/50/reenviar"), "portaria")).andExpect(status().isAccepted());
        verify(portariaService).reenviar(any(), eq(50L));
    }

    @Test
    void reenviar_comoMorador_retorna403() throws Exception {
        mvc.perform(como(post("/portaria/autorizacoes/50/reenviar"), "morador")).andExpect(status().isForbidden());
    }

    @Test
    void registrarAcesso_comoPortaria_retorna201() throws Exception {
        when(portariaService.registrarAcesso(any(), any(AcessoCreateRequest.class)))
                .thenReturn(new AcessoResponse(1L, 50L, new VisitanteResponse(7L, "Ana", null, null, null),
                        LocalDateTime.now(), null, br.com.morah.model.enums.TipoAcesso.ENTRADA, "dentro", null));

        mvc.perform(como(post("/portaria/acessos"), "portaria").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"autorizacaoId\":50,\"visitanteId\":7,\"tipo\":\"entrada\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("entrada"))
                .andExpect(jsonPath("$.status").value("dentro"));
    }

    @Test
    void registrarAcesso_semTipo_retorna400() throws Exception {
        mvc.perform(como(post("/portaria/acessos"), "portaria").contentType(MediaType.APPLICATION_JSON).content("{\"visitanteId\":7}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("tipo"));
    }

    @Test
    void listarAcessos_comoSindicoComData_retorna200() throws Exception {
        when(portariaService.listarAcessos(any(), eq(java.time.LocalDate.of(2026, 10, 2)), any()))
                .thenReturn(new AcessoPage(List.of(), new PageMetadata(0, 20, 0, 0)));

        mvc.perform(como(get("/portaria/acessos?data=2026-10-02"), "sindico")).andExpect(status().isOk());
    }

    @Test
    void listarAcessos_comoMorador_retorna403() throws Exception {
        mvc.perform(como(get("/portaria/acessos"), "morador")).andExpect(status().isForbidden());
    }
}
