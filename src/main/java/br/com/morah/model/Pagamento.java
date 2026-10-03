package br.com.morah.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pagamento (tentativa/confirmação) de uma cobrança.
 *
 * Tabela: PAGAMENTO.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cobranca", nullable = false)
    private Cobranca cobranca;

    @Column(name = "transacao_id", nullable = false)
    private String transacaoId;

    @Column(name = "meio", nullable = false)
    private String meio;

    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "pago_em", nullable = false)
    private LocalDateTime pagoEm;

    @Column(name = "status", nullable = false)
    private String status;
}
