package br.com.morah.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Decisão do morador sobre um visitante. No JSON do YAML: "autorizado" / "recusado".
 * Atenção: NÃO é guardada no banco; o service a converte em StatusAutorizacaoVisita (AUTORIZADA / RECUSADA).
 */
public enum DecisaoVisita {
    AUTORIZADO,
    RECUSADO;

    @JsonValue
    public String getValor() {
        return name().toLowerCase();
    }
}
