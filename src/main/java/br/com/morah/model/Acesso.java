package br.com.morah.model;

import br.com.morah.model.enums.TipoAcesso;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registro efetivo de entrada/saída de um visitante.
 *
 * Tabela: ACESSO.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "acesso")
@Getter
@Setter
@NoArgsConstructor
public class Acesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acesso")
    private Long id;

    // [NOVO] Condomínio do acesso: sem isso não dá para filtrar o histórico quando não há autorização.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autorizacao")
    private AutorizacaoVisita autorizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_visitante", nullable = false)
    private Visitante visitante;

    // Porteiro que registrou o acesso.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;

    @Column(name = "entrada")
    private LocalDateTime entrada;

    @Column(name = "saida")
    private LocalDateTime saida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoAcesso tipo;

    @Column(name = "status", nullable = false)
    private String status;

    // [NOVO] foto registrada na entrada/saída.
    @Column(name = "foto_url", length = 512)
    private String fotoUrl;
}
