package br.com.morah.model;

import br.com.morah.model.enums.TipoRateio;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Taxa/rateio cadastrada pelo síndico (ex.: taxa condominial de setembro). Dela nascem as cobranças.
 *
 * Tabela: TAXA.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "taxa")
@Getter
@Setter
@NoArgsConstructor
public class Taxa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_taxa")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "competencia", nullable = false, length = 7)
    private String competencia;

    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_rateio", nullable = false, length = 30)
    private TipoRateio tipoRateio;

    @Column(name = "vencimento_dia", nullable = false)
    private Integer vencimentoDia;
}
