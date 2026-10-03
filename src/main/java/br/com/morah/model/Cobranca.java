package br.com.morah.model;

import br.com.morah.model.enums.StatusCobranca;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cobrança (boleto) de uma unidade, gerada a partir de uma taxa.
 *
 * Tabela: COBRANCA.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "cobranca", indexes = @Index(name = "idx_cobranca_unidade_status", columnList = "id_unidade, status"))
@Getter
@Setter
@NoArgsConstructor
public class Cobranca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cobranca")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_taxa", nullable = false)
    private Taxa taxa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @Column(name = "competencia", nullable = false, length = 7)
    private String competencia;

    @Column(name = "vencimento", nullable = false)
    private LocalDate vencimento;

    // RISCO (SQLite): ele não tem tipo DECIMAL exato; guarda dinheiro como número de ponto flutuante (REAL), que pode gerar erros de centavos em somas grandes. Para o projeto de faculdade é aceitável; em produção (PostgreSQL) o NUMERIC é exato.
    @Column(name = "valor_original", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorOriginal;

    @Column(name = "multa", nullable = false, precision = 12, scale = 2)
    private BigDecimal multa;

    @Column(name = "juros", nullable = false, precision = 12, scale = 2)
    private BigDecimal juros;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusCobranca status;

    @Column(name = "codigo_pix", length = 512)
    private String codigoPix;
}
