package br.com.morah.repository;

import br.com.morah.model.AutorizacaoVisita;
import br.com.morah.model.enums.StatusAutorizacaoVisita;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/** Filtros opcionais da listagem de autorizações (mesma ideia do ComunicadoSpecs). */
public final class AutorizacaoVisitaSpecs {

    private AutorizacaoVisitaSpecs() {
    }

    public static Specification<AutorizacaoVisita> doCondominio(Long condominioId) {
        return (root, query, cb) -> cb.equal(root.get("unidade").get("bloco").get("condominio").get("id"), condominioId);
    }

    public static Specification<AutorizacaoVisita> daUnidade(Long unidadeId) {
        return (root, query, cb) -> cb.equal(root.get("unidade").get("id"), unidadeId);
    }

    /**
     * Filtro por status considerando a expiração, que NÃO é gravada no banco:
     * uma PENDENTE solicitada antes de "limite" já conta como EXPIRADA.
     *
     * @param limite agora - prazo de validade
     */
    public static Specification<AutorizacaoVisita> comStatus(StatusAutorizacaoVisita status, LocalDateTime limite) {
        return (root, query, cb) -> switch (status) {
            case PENDENTE -> cb.and(cb.equal(root.get("status"), StatusAutorizacaoVisita.PENDENTE),
                    cb.greaterThan(root.get("solicitadoEm"), limite));
            case EXPIRADA -> cb.or(cb.equal(root.get("status"), StatusAutorizacaoVisita.EXPIRADA),
                    cb.and(cb.equal(root.get("status"), StatusAutorizacaoVisita.PENDENTE),
                            cb.lessThanOrEqualTo(root.get("solicitadoEm"), limite)));
            default -> cb.equal(root.get("status"), status);
        };
    }
}
