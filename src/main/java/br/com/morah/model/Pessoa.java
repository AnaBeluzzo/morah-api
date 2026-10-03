package br.com.morah.model;

import br.com.morah.model.enums.StatusCadastro;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pessoa física que usa o sistema (morador, síndico, porteiro...). O CPF é único no sistema inteiro (REQ017).
 *
 * Tabela: PESSOA.
 * (Lombok gera getters/setters; @ManyToOne LAZY = só busca o objeto relacionado quando for usado.)
 */
@Entity
@Table(name = "pessoa")
@Getter
@Setter
@NoArgsConstructor
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pessoa")
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    // Mantido por fidelidade ao modelo lógico, mas o login é feito pelo Keycloak: NÃO existe endpoint de login com senha.
    @Column(name = "senha_hash")
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusCadastro status;
}
