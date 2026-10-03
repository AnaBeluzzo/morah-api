package br.com.morah.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Área comum reservável (salão de festas, churrasqueira...).
 *
 * Tabela: AREA_COMUM.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "area_comum")
@Getter
@Setter
@NoArgsConstructor
public class AreaComum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_area")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    @Column(name = "taxa", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxa;

    @Column(name = "requer_reserva", nullable = false)
    private Boolean requerReserva;

    @Column(name = "status", nullable = false)
    private String status;
}
