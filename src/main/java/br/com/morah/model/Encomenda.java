package br.com.morah.model;

import br.com.morah.model.enums.StatusEncomenda;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Encomenda recebida na portaria para uma unidade.
 *
 * Tabela: ENCOMENDA.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "encomenda")
@Getter
@Setter
@NoArgsConstructor
public class Encomenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encomenda")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    // Porteiro que recebeu a encomenda.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;

    @Column(name = "remetente")
    private String remetente;

    @Column(name = "transportadora", nullable = false)
    private String transportadora;

    @Column(name = "codigo_rastreio")
    private String codigoRastreio;

    @Column(name = "codigo_retirada")
    private String codigoRetirada;

    @Column(name = "recebida_em", nullable = false)
    private LocalDateTime recebidaEm;

    @Column(name = "retirada_em")
    private LocalDateTime retiradaEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusEncomenda status;
}
