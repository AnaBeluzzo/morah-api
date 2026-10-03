package br.com.morah.dto;

import br.com.morah.model.enums.PrioridadeAviso;

import java.time.LocalDateTime;

/** Saída: schema "Aviso" do YAML (+ categoria e publicoAlvo, que a edição/listagem precisam mostrar). */
public record AvisoResponse(
        Long id,
        String titulo,
        String conteudo,
        PrioridadeAviso prioridade,
        String categoria,
        PublicoAlvoDto publicoAlvo,
        PessoaResumo autor,
        LocalDateTime publicadoEm,
        LocalDateTime expiraEm,
        boolean lidoPeloUsuario) {
}
