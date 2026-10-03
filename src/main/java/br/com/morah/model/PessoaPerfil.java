package br.com.morah.model;

import br.com.morah.model.enums.StatusCadastro;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Liga uma pessoa a um perfil EM UM condomínio (a mesma pessoa pode ser síndica num e moradora noutro).
 *
 * Tabela: PESSOA_PERFIL.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "pessoa_perfil", uniqueConstraints = @UniqueConstraint(columnNames = {"id_pessoa", "id_perfil", "id_condominio"}))
@Getter
@Setter
@NoArgsConstructor
public class PessoaPerfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pessoa_perfil")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusCadastro status;
}
