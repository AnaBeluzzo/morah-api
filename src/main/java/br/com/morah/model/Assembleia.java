package br.com.morah.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Assembleia (presencial ou virtual) do condomínio.
 *
 * Tabela: ASSEMBLEIA.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "assembleia")
@Getter
@Setter
@NoArgsConstructor
public class Assembleia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_assembleia")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao", nullable = false, length = 4000)
    private String descricao;

    @Column(name = "modalidade", nullable = false)
    private String modalidade;

    @Column(name = "inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fim", nullable = false)
    private LocalDateTime fim;

    @Column(name = "quorum_minimo", nullable = false, precision = 5, scale = 2)
    private BigDecimal quorumMinimo;

    @Column(name = "status", nullable = false)
    private String status;
}
