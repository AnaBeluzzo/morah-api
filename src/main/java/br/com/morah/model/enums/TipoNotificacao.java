package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valores possíveis de TipoNotificacao.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "PORTARIA".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "portaria" -> é o que @JsonValue faz.
 */
public enum TipoNotificacao {
    PORTARIA,
    ENCOMENDA,
    FINANCEIRO,
    AVISO,
    RESERVA;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
