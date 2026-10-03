package br.com.morah.dto;

/** Schema Visitante do YAML (VisitanteInput + id + observacao). */
public record VisitanteResponse(Long id, String nome, String documento, String telefone, String observacao) {
}
