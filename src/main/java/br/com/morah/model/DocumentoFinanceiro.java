package br.com.morah.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Documento financeiro publicado (nota fiscal, prestação de contas).
 *
 * Tabela: DOCUMENTO_FINANCEIRO.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "documento_financeiro")
@Getter
@Setter
@NoArgsConstructor
public class DocumentoFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "arquivo", nullable = false, length = 512)
    private String arquivo;

    @Column(name = "competencia")
    private LocalDate competencia;

    @Column(name = "publicado_em", nullable = false)
    private LocalDateTime publicadoEm;
}
