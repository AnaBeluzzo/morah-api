package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valores possíveis de StatusEncomenda.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "AGUARDANDO_RETIRADA".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "aguardando_retirada" -> é o que @JsonValue faz.
 */
public enum StatusEncomenda {
    AGUARDANDO_RETIRADA,
    RETIRADA,
    EXTRAVIADA;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
