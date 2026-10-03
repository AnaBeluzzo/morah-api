package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valores possíveis de StatusCobranca.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "PENDENTE".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "pendente" -> é o que @JsonValue faz.
 */
public enum StatusCobranca {
    PENDENTE,
    PAGO,
    ATRASADO,
    CANCELADO;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
