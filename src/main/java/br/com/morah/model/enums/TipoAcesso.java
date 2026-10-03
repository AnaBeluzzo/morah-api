package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valores possíveis de TipoAcesso.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "ENTRADA".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "entrada" -> é o que @JsonValue faz.
 */
public enum TipoAcesso {
    ENTRADA,
    SAIDA;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
