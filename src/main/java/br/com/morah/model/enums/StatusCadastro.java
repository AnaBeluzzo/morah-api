package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valores possíveis de StatusCadastro.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "ATIVO".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "ativo" -> é o que @JsonValue faz.
 */
public enum StatusCadastro {
    ATIVO,
    INATIVO;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
