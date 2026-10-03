package br.com.morah.repository;

import br.com.morah.model.LeituraComunicado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Set;

public interface LeituraComunicadoRepository extends JpaRepository<LeituraComunicado, Long> {

    boolean existsByComunicadoIdAndPessoaId(Long comunicadoId, Long pessoaId);

    /** Dentre os avisos de uma página, quais esta pessoa já leu? Uma única consulta para a página toda. */
    @Query("select l.comunicado.id from LeituraComunicado l where l.pessoa.id = :pessoaId and l.comunicado.id in :comunicadoIds")
    Set<Long> findComunicadoIdsLidos(Long pessoaId, Collection<Long> comunicadoIds);
}
