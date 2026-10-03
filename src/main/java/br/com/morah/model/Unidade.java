package br.com.morah.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Unidade (apartamento/casa) dentro de um bloco.
 *
 * Tabela: UNIDADE.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "unidade")
@Getter
@Setter
@NoArgsConstructor
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidade")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bloco", nullable = false)
    private Bloco bloco;

    @Column(name = "identificacao", nullable = false)
    private String identificacao;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "area_m2", nullable = false, precision = 10, scale = 2)
    private BigDecimal areaM2;

    @Column(name = "fracao_ideal", nullable = false, precision = 10, scale = 6)
    private BigDecimal fracaoIdeal;

    @Column(name = "status", nullable = false)
    private String status;
}
