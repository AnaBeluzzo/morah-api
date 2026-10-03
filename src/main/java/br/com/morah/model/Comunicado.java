package br.com.morah.model;

import br.com.morah.model.enums.PrioridadeAviso;
import br.com.morah.model.enums.TipoPublicoAlvo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Comunicado = o "Aviso" do contrato OpenAPI. É a entidade do módulo de referência.
 *
 * Tabela: COMUNICADO.
 *
 * ATENÇÃO - AJUSTE PROPOSTO NO MODELO LÓGICO:
 * O diagrama só tem id, condominio, pessoa, titulo, conteudo, prioridade e publicado_em.
 * O morah-api.yaml exige mais coisas, então ACRESCENTEI as colunas abaixo (marcadas com [NOVO]):
 *   - categoria          -> filtro ?categoria=manutencao (UC25-A1)
 *   - expira_em          -> "vigência": fora dela o GET devolve 410 (UC25-E1)
 *   - publico_alvo_tipo, id_bloco, id_unidade -> público-alvo: condomínio, bloco ou unidade (UC31)
 * E a nova tabela LEITURA_COMUNICADO (ver LeituraComunicado) guarda o "lidoPeloUsuario".
 * Se a equipe preferir, esses campos podem ser removidos do YAML em vez de entrarem no banco.
 */
@Entity
@Table(name = "comunicado", indexes = {
        @Index(name = "idx_comunicado_condominio_publicado", columnList = "id_condominio, publicado_em")
})
@Getter
@Setter
@NoArgsConstructor
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comunicado")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    /** Autor do aviso (o síndico que publicou). A coluna do modelo se chama id_pessoa. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false)
    private Pessoa autor;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "conteudo", nullable = false, length = 4000)
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade", nullable = false, length = 30)
    private PrioridadeAviso prioridade;

    // [NOVO] categoria livre (ex.: "manutencao", "geral"). Opcional.
    @Column(name = "categoria", length = 50)
    private String categoria;

    @Column(name = "publicado_em", nullable = false)
    private LocalDateTime publicadoEm;

    // [NOVO] null = nunca expira.
    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    // [NOVO] a quem o aviso se destina. Se BLOCO, "bloco" é preenchido; se UNIDADE, "unidade" é preenchida.
    @Enumerated(EnumType.STRING)
    @Column(name = "publico_alvo_tipo", nullable = false, length = 30)
    private TipoPublicoAlvo publicoAlvoTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bloco")
    private Bloco bloco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;

    /** Regra de negócio simples que mora na própria entidade: o aviso está fora de vigência? */
    public boolean estaForaDeVigencia(LocalDateTime agora) {
        return expiraEm != null && !expiraEm.isAfter(agora);
    }
}
