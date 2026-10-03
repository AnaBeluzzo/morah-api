package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valores possíveis de StatusAutorizacaoRetirada.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "ATIVA".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "ativa" -> é o que @JsonValue faz.
 */
public enum StatusAutorizacaoRetirada {
    ATIVA,
    UTILIZADA,
    EXPIRADA;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
