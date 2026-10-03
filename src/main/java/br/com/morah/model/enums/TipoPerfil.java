package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Perfis de acesso do sistema. PROPRIETARIO é cumulativo ao MORADOR (tem tudo que o morador tem + extras).
 *
 * No banco (@Enumerated(EnumType.STRING)) guardamos o NOME da constante, ex.: "MORADOR".
 * No JSON (API) o contrato OpenAPI usa minúsculas, ex.: "morador" -> é o que @JsonValue faz.
 */
public enum TipoPerfil {
    MORADOR,
    PROPRIETARIO,
    SINDICO,
    PORTARIA;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
