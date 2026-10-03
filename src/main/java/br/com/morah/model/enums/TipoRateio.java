package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valores possíveis de TipoRateio.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "IGUALITARIO".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "igualitario" -> é o que @JsonValue faz.
 */
public enum TipoRateio {
    IGUALITARIO,
    FRACAO_IDEAL;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
