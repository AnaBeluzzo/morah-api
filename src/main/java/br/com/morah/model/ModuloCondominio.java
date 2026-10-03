package br.com.morah.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Quais módulos estão ativos em cada condomínio.
 *
 * Tabela: MODULO_CONDOMINIO.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "modulo_condominio", uniqueConstraints = @UniqueConstraint(columnNames = {"id_condominio", "id_modulo"}))
@Getter
@Setter
@NoArgsConstructor
public class ModuloCondominio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo_condominio")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modulo", nullable = false)
    private Modulo modulo;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}
