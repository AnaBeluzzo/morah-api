package br.com.morah.dto;

import br.com.morah.model.enums.PrioridadeAviso;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Entrada do POST /avisos (schema AvisoCreateRequest). As anotações são Bean Validation:
 * o Spring valida automaticamente por causa do @Valid no controller e responde 400 se falhar.
 *
 * "categoria" NÃO está no schema do YAML; foi acrescentada (opcional) porque o filtro ?categoria precisa de dados.
 */
public record AvisoCreateRequest(
        @NotBlank(message = "titulo é obrigatório") @Size(max = 150, message = "titulo deve ter no máximo 150 caracteres") String titulo,
        @NotBlank(message = "conteudo é obrigatório") @Size(max = 4000, message = "conteudo deve ter no máximo 4000 caracteres") String conteudo,
        PrioridadeAviso prioridade,
        @Size(max = 50, message = "categoria deve ter no máximo 50 caracteres") String categoria,
        @NotNull(message = "publicoAlvo é obrigatório") @Valid PublicoAlvoDto publicoAlvo,
        @Future(message = "expiraEm deve estar no futuro") LocalDateTime expiraEm) {
}
