package br.com.morah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Módulo funcional do sistema (ex.: portaria, reservas) que pode ser ligado/desligado.
 *
 * Tabela: MODULO.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "modulo")
@Getter
@Setter
@NoArgsConstructor
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo")
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "chave", nullable = false, unique = true)
    private String chave;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "obrigatorio", nullable = false)
    private Boolean obrigatorio;
}
