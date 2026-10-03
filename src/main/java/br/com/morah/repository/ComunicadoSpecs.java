package br.com.morah.repository;

import br.com.morah.model.Comunicado;
import br.com.morah.model.enums.PrioridadeAviso;
import br.com.morah.model.enums.TipoPublicoAlvo;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * "Peças de filtro" reutilizáveis. Cada método devolve uma condição do WHERE;
 * o service combina só as que forem necessárias com .and(...).
 * É o jeito limpo de fazer filtros opcionais (categoria e prioridade podem vir vazios).
 */
public final class ComunicadoSpecs {

    private ComunicadoSpecs() {
    }

    public static Specification<Comunicado> doCondominio(Long condominioId) {
        return (root, query, cb) -> cb.equal(root.get("condominio").get("id"), condominioId);
    }

    /** Vigente = sem data de expiração OU expiração ainda no futuro. */
    public static Specification<Comunicado> vigente(LocalDateTime agora) {
        return (root, query, cb) -> cb.or(cb.isNull(root.get("expiraEm")), cb.greaterThan(root.get("expiraEm"), agora));
    }

    public static Specification<Comunicado> comCategoria(String categoria) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("categoria")), categoria.toLowerCase());
    }

    public static Specification<Comunicado> comPrioridade(PrioridadeAviso prioridade) {
        return (root, query, cb) -> cb.equal(root.get("prioridade"), prioridade);
    }

    /**
     * Avisos que um morador/proprietário pode ver: os do condomínio inteiro,
     * os do SEU bloco e os da SUA unidade. (blocoId/unidadeId podem ser null se o usuário não tem unidade.)
     */
    public static Specification<Comunicado> destinadoA(Long blocoId, Long unidadeId) {
        return (root, query, cb) -> {
            List<Predicate> opcoes = new ArrayList<>();
            opcoes.add(cb.equal(root.get("publicoAlvoTipo"), TipoPublicoAlvo.CONDOMINIO));
            if (blocoId != null) {
                opcoes.add(cb.and(cb.equal(root.get("publicoAlvoTipo"), TipoPublicoAlvo.BLOCO),
                        cb.equal(root.get("bloco").get("id"), blocoId)));
            }
            if (unidadeId != null) {
                opcoes.add(cb.and(cb.equal(root.get("publicoAlvoTipo"), TipoPublicoAlvo.UNIDADE),
                        cb.equal(root.get("unidade").get("id"), unidadeId)));
            }
            return cb.or(opcoes.toArray(new Predicate[0]));
        };
    }
}
