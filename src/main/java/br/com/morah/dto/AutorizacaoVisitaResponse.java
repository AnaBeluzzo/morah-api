package br.com.morah.dto;

import br.com.morah.model.enums.StatusAutorizacaoVisita;

import java.time.LocalDateTime;

/** Saída: schema AutorizacaoVisita do YAML (+ fotoUrl, campo novo). */
public record AutorizacaoVisitaResponse(
        Long id,
        VisitanteResponse visitante,
        Long unidadeId,
        String motivo,
        String fotoUrl,
        LocalDateTime solicitadoEm,
        LocalDateTime respondidoEm,
        StatusAutorizacaoVisita status) {
}
