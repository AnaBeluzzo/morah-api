package br.com.morah.repository;

import br.com.morah.model.AutorizacaoVisita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AutorizacaoVisitaRepository extends JpaRepository<AutorizacaoVisita, Long>, JpaSpecificationExecutor<AutorizacaoVisita> {

    /** unidade.bloco.condominio.id: garante que a autorização é do condomínio do usuário. */
    Optional<AutorizacaoVisita> findByIdAndUnidadeBlocoCondominioId(Long id, Long condominioId);

    /** Carrega visitante e unidade no mesmo SELECT (evita consultas extras por linha). */
    @Override
    @EntityGraph(attributePaths = {"visitante", "unidade"})
    Page<AutorizacaoVisita> findAll(Specification<AutorizacaoVisita> spec, Pageable pageable);
}
