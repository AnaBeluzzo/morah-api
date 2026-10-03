package br.com.morah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Item de pauta de uma assembleia.
 *
 * Tabela: PAUTA.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "pauta")
@Getter
@Setter
@NoArgsConstructor
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pauta")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_assembleia", nullable = false)
    private Assembleia assembleia;

    @Column(name = "ordem", nullable = false)
    private Integer ordem;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao", nullable = false, length = 4000)
    private String descricao;

    @Column(name = "tipo_votacao", nullable = false)
    private String tipoVotacao;
}
