package br.com.morah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bloco (ou torre) de um condomínio.
 *
 * Tabela: BLOCO.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "bloco")
@Getter
@Setter
@NoArgsConstructor
public class Bloco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloco")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "qtd_andares", nullable = false)
    private Integer qtdAndares;
}
