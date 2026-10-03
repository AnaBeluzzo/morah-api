package br.com.morah.dto;

import br.com.morah.model.enums.DecisaoVisita;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entrada do PATCH /portaria/autorizacoes/{id}/decisao.
 * "justificativa" é aceita, mas ainda não é gravada: a tabela AUTORIZACAO_VISITA não tem coluna para ela.
 */
public record DecisaoAutorizacaoRequest(
        @NotNull(message = "decisao é obrigatória") DecisaoVisita decisao,
        @Size(max = 255, message = "justificativa deve ter no máximo 255 caracteres") String justificativa) {
}
