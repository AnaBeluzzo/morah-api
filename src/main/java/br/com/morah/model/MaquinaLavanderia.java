package br.com.morah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Máquina da lavanderia coletiva.
 *
 * Tabela: MAQUINA_LAVANDERIA.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "maquina_lavanderia")
@Getter
@Setter
@NoArgsConstructor
public class MaquinaLavanderia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_maquina")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "status", nullable = false)
    private String status;
}
