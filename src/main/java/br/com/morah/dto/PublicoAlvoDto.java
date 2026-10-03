package br.com.morah.dto;

import br.com.morah.model.enums.TipoPublicoAlvo;
import jakarta.validation.constraints.NotNull;

/** Schema PublicoAlvo do YAML. blocoId é exigido quando tipo=bloco; unidadeId quando tipo=unidade (validado no service). */
public record PublicoAlvoDto(
        @NotNull(message = "tipo é obrigatório") TipoPublicoAlvo tipo,
        Long blocoId,
        Long unidadeId) {
}
