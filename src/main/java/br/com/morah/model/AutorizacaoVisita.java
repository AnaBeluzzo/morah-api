package br.com.morah.model;

import br.com.morah.model.enums.StatusAutorizacaoVisita;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pedido de autorização de entrada de um visitante, enviado ao morador da unidade.
 *
 * Tabela: AUTORIZACAO_VISITA.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "autorizacao_visita")
@Getter
@Setter
@NoArgsConstructor
public class AutorizacaoVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_autorizacao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_visitante", nullable = false)
    private Visitante visitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    // [AJUSTADO] Quem DECIDIU o pedido (o morador). null enquanto estiver PENDENTE.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa")
    private Pessoa pessoa;

    @Column(name = "solicitado_em", nullable = false)
    private LocalDateTime solicitadoEm;

    @Column(name = "respondido_em")
    private LocalDateTime respondidoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusAutorizacaoVisita status;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    // [NOVO] URL da foto do visitante tirada na portaria.
    @Column(name = "foto_url", length = 512)
    private String fotoUrl;
}
