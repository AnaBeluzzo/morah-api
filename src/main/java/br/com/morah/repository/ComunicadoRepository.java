package br.com.morah.repository;

import br.com.morah.model.Comunicado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * JpaSpecificationExecutor permite montar filtros opcionais dinamicamente (ver ComunicadoSpecs).
 */
public interface ComunicadoRepository extends JpaRepository<Comunicado, Long>, JpaSpecificationExecutor<Comunicado> {

    /**
     * Query derivada: o Spring lê o NOME do método e cria o SQL.
     * "ComunicadoId" + "CondominioId" -> where id = ? and condominio.id = ?
     * Filtrar por condomínio garante que um síndico nunca enxergue avisos de outro condomínio.
     */
    Optional<Comunicado> findByIdAndCondominioId(Long id, Long condominioId);

    /**
     * Mesma busca paginada do JpaSpecificationExecutor, mas já carregando o autor no mesmo SELECT
     * (@EntityGraph) para evitar uma consulta extra por aviso (o famoso problema "N+1").
     */
    @Override
    @EntityGraph(attributePaths = "autor")
    Page<Comunicado> findAll(Specification<Comunicado> spec, Pageable pageable);
}
