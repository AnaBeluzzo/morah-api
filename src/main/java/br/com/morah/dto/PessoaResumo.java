package br.com.morah.dto;

/**
 * Schema PessoaResumo do YAML. Um "record" é uma classe imutável enxuta: o Java gera
 * construtor, getters (id(), nome()...), equals e toString. Ideal para DTOs.
 * Repare que NÃO expõe cpf nem senha_hash: DTO mostra só o que a API deve mostrar.
 */
public record PessoaResumo(Long id, String nome, String telefone) {
}
