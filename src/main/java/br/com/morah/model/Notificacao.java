package br.com.morah.model;

import br.com.morah.model.enums.TipoNotificacao;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Notificação interna para uma pessoa.
 *
 * Tabela: NOTIFICACAO.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "notificacao")
@Getter
@Setter
@NoArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoNotificacao tipo;

    // Id do objeto relacionado (aviso, encomenda...). Referência solta de propósito: aponta para tabelas diferentes conforme o tipo, então não pode ser FK.
    @Column(name = "id_referencia")
    private Long idReferencia;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "mensagem", nullable = false, length = 1000)
    private String mensagem;

    @Column(name = "enviada_em", nullable = false)
    private LocalDateTime enviadaEm;

    @Column(name = "lida_em")
    private LocalDateTime lidaEm;
}
