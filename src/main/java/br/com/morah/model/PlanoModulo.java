package br.com.morah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Quais módulos cada plano inclui.
 *
 * Tabela: PLANO_MODULO.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "plano_modulo", uniqueConstraints = @UniqueConstraint(columnNames = {"id_plano", "id_modulo"}))
@Getter
@Setter
@NoArgsConstructor
public class PlanoModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plano_modulo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plano", nullable = false)
    private Plano plano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modulo", nullable = false)
    private Modulo modulo;

    @Column(name = "incluso", nullable = false)
    private Boolean incluso;
}
