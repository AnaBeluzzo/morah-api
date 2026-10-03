package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Prioridade de um aviso. URGENTE fica fixado no topo do mural.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "NORMAL".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "normal" -> é o que @JsonValue faz.
 */
public enum PrioridadeAviso {
    NORMAL,
    URGENTE;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
