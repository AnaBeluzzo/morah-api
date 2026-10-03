package br.com.morah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Visitante cadastrado pela portaria.
 *
 * Tabela: VISITANTE.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "visitante")
@Getter
@Setter
@NoArgsConstructor
public class Visitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_visitante")
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "documento")
    private String documento;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "observacao")
    private String observacao;
}
