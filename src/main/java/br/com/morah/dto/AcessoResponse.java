package br.com.morah.dto;

import br.com.morah.model.enums.TipoAcesso;

import java.time.LocalDateTime;

/** Saída: schema Acesso do YAML (+ fotoUrl, campo novo). */
public record AcessoResponse(
        Long id,
        Long autorizacaoId,
        VisitanteResponse visitante,
        LocalDateTime entrada,
        LocalDateTime saida,
        TipoAcesso tipo,
        String status,
        String fotoUrl) {
}
