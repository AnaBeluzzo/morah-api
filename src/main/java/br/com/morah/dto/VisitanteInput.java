package br.com.morah.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados do visitante enviados pela portaria.
 */
public record VisitanteInput(
        @NotBlank(message = "nome é obrigatório") @Size(max = 150, message = "nome deve ter no máximo 150 caracteres") String nome,
        @Size(max = 30, message = "documento deve ter no máximo 30 caracteres") String documento,
        @Size(max = 20, message = "telefone deve ter no máximo 20 caracteres") String telefone) {
}