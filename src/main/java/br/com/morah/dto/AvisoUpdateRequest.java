package br.com.morah.dto;

import br.com.morah.model.enums.PrioridadeAviso;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Entrada do PATCH /avisos/{id}. PATCH = atualização PARCIAL: todos os campos são opcionais e
 * só os que vierem preenchidos (diferentes de null) são alterados.
 * Limitação: por isso não dá para "limpar" o expiraEm voltando a null.
 */
public record AvisoUpdateRequest(
        @Pattern(regexp = ".*\\S.*", message = "titulo não pode ser vazio") @Size(max = 150) String titulo,
        @Pattern(regexp = "(?s).*\\S.*", message = "conteudo não pode ser vazio") @Size(max = 4000) String conteudo,
        PrioridadeAviso prioridade,
        @Size(max = 50) String categoria,
        @Future(message = "expiraEm deve estar no futuro") LocalDateTime expiraEm) {
}
