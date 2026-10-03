package br.com.morah.dto;

import br.com.morah.model.enums.TipoAcesso;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entrada do POST /portaria/acessos (schema AcessoCreateRequest).
 */
public record AcessoCreateRequest(
        Long autorizacaoId,
        @NotNull(message = "visitanteId é obrigatório") Long visitanteId,
        @NotNull(message = "tipo é obrigatório") TipoAcesso tipo,
        @Size(max = 512, message = "fotoUrl deve ter no máximo 512 caracteres")
        @Pattern(regexp = "^https?://.*", message = "fotoUrl deve ser uma URL http(s)") String fotoUrl) {
}