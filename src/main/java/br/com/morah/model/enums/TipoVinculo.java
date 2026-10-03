package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valores possíveis de TipoVinculo.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "PROPRIETARIO".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "proprietario" -> é o que @JsonValue faz.
 */
public enum TipoVinculo {
    PROPRIETARIO,
    INQUILINO,
    DEPENDENTE;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
