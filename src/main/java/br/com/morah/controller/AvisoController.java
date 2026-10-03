package br.com.morah.controller;

import br.com.morah.config.ContextoUsuario;
import br.com.morah.config.PerfisPermitidos;
import br.com.morah.dto.AvisoCreateRequest;
import br.com.morah.dto.AvisoPage;
import br.com.morah.dto.AvisoResponse;
import br.com.morah.dto.AvisoUpdateRequest;
import br.com.morah.model.enums.PrioridadeAviso;
import br.com.morah.model.enums.TipoPerfil;
import br.com.morah.service.AvisoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * C de "Controller": a porta de entrada HTTP. Faz só três coisas:
 *   1) recebe e valida a requisição (@Valid);
 *   2) chama o Service;
 *   3) devolve DTO com o status HTTP certo.
 * Nenhuma regra de negócio aqui, e nunca devolvemos Entity.
 *
 * O caminho completo é /v1/avisos: "/v1" vem do context-path no application.yml.
 */
@RestController
@RequestMapping("/avisos")
@RequiredArgsConstructor
@Tag(name = "Avisos", description = "Mural de comunicados do condomínio")
public class AvisoController {

    private final AvisoService avisoService;

    @GetMapping
    @PerfisPermitidos({TipoPerfil.MORADOR, TipoPerfil.PROPRIETARIO, TipoPerfil.SINDICO, TipoPerfil.PORTARIA})
    @Operation(summary = "Listar avisos vigentes visíveis ao contexto ativo")
    public AvisoPage listar(
            @Parameter(hidden = true) ContextoUsuario contexto,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) PrioridadeAviso prioridade,
            // Ordenação padrão: urgentes primeiro (URGENTE > NORMAL em ordem alfabética decrescente), depois os mais recentes.
            @ParameterObject @PageableDefault(size = 20, sort = {"prioridade", "publicadoEm"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return avisoService.listar(contexto, categoria, prioridade, pageable);
    }

    @PostMapping
    @PerfisPermitidos(TipoPerfil.SINDICO)
    @Operation(summary = "Publicar um novo aviso")
    public ResponseEntity<AvisoResponse> criar(@Parameter(hidden = true) ContextoUsuario contexto,
                                               @Valid @RequestBody AvisoCreateRequest requisicao) {
        AvisoResponse criado = avisoService.criar(contexto, requisicao);
        // 201 Created + header Location apontando para o novo recurso (boa prática REST).
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{avisoId}")
    @PerfisPermitidos({TipoPerfil.MORADOR, TipoPerfil.PROPRIETARIO, TipoPerfil.SINDICO, TipoPerfil.PORTARIA})
    @Operation(summary = "Detalhar um aviso (marca como lido para o usuário)")
    public AvisoResponse buscar(@Parameter(hidden = true) ContextoUsuario contexto, @PathVariable Long avisoId) {
        return avisoService.buscar(contexto, avisoId);
    }

    @PatchMapping("/{avisoId}")
    @PerfisPermitidos(TipoPerfil.SINDICO)
    @Operation(summary = "Editar um aviso existente")
    public AvisoResponse atualizar(@Parameter(hidden = true) ContextoUsuario contexto, @PathVariable Long avisoId,
                                   @Valid @RequestBody AvisoUpdateRequest requisicao) {
        return avisoService.atualizar(contexto, avisoId, requisicao);
    }

    @DeleteMapping("/{avisoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PerfisPermitidos(TipoPerfil.SINDICO)
    @Operation(summary = "Remover/expirar um aviso")
    public void remover(@Parameter(hidden = true) ContextoUsuario contexto, @PathVariable Long avisoId) {
        avisoService.remover(contexto, avisoId);
    }
}
