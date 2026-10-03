package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Para quem o aviso é destinado: todo o condomínio, um bloco ou uma unidade.
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "CONDOMINIO".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "condominio" -> é o que @JsonValue faz.
 */
public enum TipoPublicoAlvo {
    CONDOMINIO,
    BLOCO,
    UNIDADE;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
