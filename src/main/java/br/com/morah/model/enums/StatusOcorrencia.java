package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valores possíveis de StatusOcorrencia.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "ABERTA".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "aberta" -> é o que @JsonValue faz.
 */
public enum StatusOcorrencia {
    ABERTA,
    EM_ANDAMENTO,
    ENCERRADA;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
