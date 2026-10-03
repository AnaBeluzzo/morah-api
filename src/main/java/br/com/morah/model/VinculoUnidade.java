package br.com.morah.model;

import br.com.morah.model.enums.TipoVinculo;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Vínculo de uma pessoa com uma unidade (proprietário, inquilino ou dependente), com período de vigência.
 *
 * Tabela: VINCULO_UNIDADE.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "vinculo_unidade")
@Getter
@Setter
@NoArgsConstructor
public class VinculoUnidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vinculo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vinculo", nullable = false, length = 30)
    private TipoVinculo tipoVinculo;

    @Column(name = "principal", nullable = false)
    private Boolean principal;

    @Column(name = "inicio", nullable = false)
    private LocalDate inicio;

    @Column(name = "fim")
    private LocalDate fim;
}
