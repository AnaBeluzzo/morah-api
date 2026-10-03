package br.com.morah.controller;

import br.com.morah.config.ContextoUsuario;
import br.com.morah.config.PerfisPermitidos;
import br.com.morah.dto.*;
import br.com.morah.model.enums.StatusAutorizacaoVisita;
import br.com.morah.model.enums.TipoPerfil;
import br.com.morah.service.PortariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

/** Endpoints da tag Portaria do YAML. Só recebe HTTP e delega ao PortariaService. */
@RestController
@RequestMapping("/portaria")
@RequiredArgsConstructor
@Tag(name = "Portaria", description = "Autorização e registro de entrada de visitantes")
public class PortariaController {

    private final PortariaService portariaService;

    @PostMapping("/visitantes") 
    @PerfisPermitidos(TipoPerfil.PORTARIA)
    @Operation(summary = "Registrar um visitante e solicitar autorização de entrada ao morador")
    public ResponseEntity<AutorizacaoVisitaResponse> registrarVisitante(
            @Parameter(hidden = true) ContextoUsuario contexto,
            @Valid @RequestBody AutorizacaoVisitaCreateRequest requisicao) {
        AutorizacaoVisitaResponse criada = portariaService.registrarVisitante(contexto, requisicao);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/portaria/autorizacoes/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping("/autorizacoes")
    @PerfisPermitidos({TipoPerfil.PORTARIA, TipoPerfil.MORADOR, TipoPerfil.PROPRIETARIO})
    @Operation(summary = "Listar autorizações de visita (fila da guarita ou pendências do morador)")
    public AutorizacaoVisitaPage listarAutorizacoes(
            @Parameter(hidden = true) ContextoUsuario contexto,
            @RequestParam(required = false) StatusAutorizacaoVisita status,
            @ParameterObject @PageableDefault(size = 20, sort = "solicitadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        return portariaService.listarAutorizacoes(contexto, status, pageable);
    }

    @GetMapping("/autorizacoes/{autorizacaoId}")
    @PerfisPermitidos({TipoPerfil.PORTARIA, TipoPerfil.MORADOR, TipoPerfil.PROPRIETARIO})
    @Operation(summary = "Detalhar uma autorização de visita")
    public AutorizacaoVisitaResponse buscarAutorizacao(@Parameter(hidden = true) ContextoUsuario contexto,
                                                       @PathVariable Long autorizacaoId) {
        return portariaService.buscarAutorizacao(contexto, autorizacaoId);
    }

    @PatchMapping("/autorizacoes/{autorizacaoId}/decisao")
    @PerfisPermitidos({TipoPerfil.MORADOR, TipoPerfil.PROPRIETARIO})
    @Operation(summary = "Autorizar ou recusar a entrada de um visitante")
    public AutorizacaoVisitaResponse decidir(@Parameter(hidden = true) ContextoUsuario contexto,
                                             @PathVariable Long autorizacaoId,
                                             @Valid @RequestBody DecisaoAutorizacaoRequest requisicao) {
        return portariaService.decidir(contexto, autorizacaoId, requisicao);
    }

    @PostMapping("/autorizacoes/{autorizacaoId}/reenviar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PerfisPermitidos(TipoPerfil.PORTARIA)
    @Operation(summary = "Reenviar a notificação de autorização pendente")
    public void reenviar(@Parameter(hidden = true) ContextoUsuario contexto, @PathVariable Long autorizacaoId) {
        portariaService.reenviar(contexto, autorizacaoId);
    }

    @GetMapping("/acessos")
    @PerfisPermitidos({TipoPerfil.PORTARIA, TipoPerfil.SINDICO})
    @Operation(summary = "Consultar o histórico de acessos do condomínio")
    public AcessoPage listarAcessos(
            @Parameter(hidden = true) ContextoUsuario contexto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @ParameterObject @PageableDefault(size = 20, sort = "entrada", direction = Sort.Direction.DESC) Pageable pageable) {
        return portariaService.listarAcessos(contexto, data, pageable);
    }

    @PostMapping("/acessos")
    @PerfisPermitidos(TipoPerfil.PORTARIA)
    @Operation(summary = "Registrar a entrada ou saída efetiva de um visitante já autorizado")
    public ResponseEntity<AcessoResponse> registrarAcesso(@Parameter(hidden = true) ContextoUsuario contexto,
                                                          @Valid @RequestBody AcessoCreateRequest requisicao) {
        AcessoResponse registrado = portariaService.registrarAcesso(contexto, requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrado);
    }
}
