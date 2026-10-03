package br.com.morah.model;

import br.com.morah.model.enums.StatusAutorizacaoRetirada;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Autorização temporária para um terceiro retirar uma encomenda.
 *
 * Tabela: AUTORIZACAO_RETIRADA.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "autorizacao_retirada")
@Getter
@Setter
@NoArgsConstructor
public class AutorizacaoRetirada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_autorizacao_retirada")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_encomenda", nullable = false)
    private Encomenda encomenda;

    // Morador que autorizou.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;

    @Column(name = "nome_terceiro", nullable = false)
    private String nomeTerceiro;

    @Column(name = "documento", nullable = false)
    private String documento;

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "validade", nullable = false)
    private LocalDateTime validade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusAutorizacaoRetirada status;
}
