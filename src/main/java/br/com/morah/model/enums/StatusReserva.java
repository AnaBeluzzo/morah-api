package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valores possíveis de StatusReserva.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "CONFIRMADA".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "confirmada" -> é o que @JsonValue faz.
 */
public enum StatusReserva {
    CONFIRMADA,
    CANCELADA,
    CONCLUIDA,
    NAO_COMPARECEU;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
