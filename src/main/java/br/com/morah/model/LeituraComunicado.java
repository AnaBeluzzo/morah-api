package br.com.morah.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * [NOVA TABELA, não existe no diagrama] Registra que uma pessoa leu um comunicado.
 * Necessária para o campo "lidoPeloUsuario" do Aviso e para o UC25 ("Sistema registra a leitura").
 * A restrição única impede registrar a mesma leitura duas vezes.
 */
@Entity
@Table(name = "leitura_comunicado", uniqueConstraints = @UniqueConstraint(columnNames = {"id_comunicado", "id_pessoa"}))
@Getter
@Setter
@NoArgsConstructor
public class LeituraComunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_leitura")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comunicado", nullable = false)
    private Comunicado comunicado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa pessoa;

    @Column(name = "lido_em", nullable = false)
    private LocalDateTime lidoEm;
}
