package br.com.morah.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entrada do POST /portaria/visitantes (schema AutorizacaoVisitaCreateRequest).
 */
public record AutorizacaoVisitaCreateRequest(
        @NotNull(message = "unidadeId é obrigatório") Long unidadeId,
        @NotNull(message = "visitante é obrigatório") @Valid VisitanteInput visitante,
        @NotBlank(message = "motivo é obrigatório") @Size(max = 255, message = "motivo deve ter no máximo 255 caracteres") String motivo,
        @Size(max = 512, message = "fotoUrl deve ter no máximo 512 caracteres")
        @Pattern(regexp = "^https?://.*", message = "fotoUrl deve ser uma URL http(s)") String fotoUrl) {
}