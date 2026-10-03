package br.com.morah.repository;

import br.com.morah.model.Acesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AcessoRepository extends JpaRepository<Acesso, Long> {

    @EntityGraph(attributePaths = "visitante")
    Page<Acesso> findByCondominioId(Long condominioId, Pageable pageable);

    /** Histórico de um dia: entrada entre 00:00 (inclusive) e 24:00 (o chamador passa os dois limites). */
    @EntityGraph(attributePaths = "visitante")
    Page<Acesso> findByCondominioIdAndEntradaBetween(Long condominioId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    /** O visitante está dentro? = existe acesso dele neste condomínio com entrada e sem saída. */
    Optional<Acesso> findFirstByCondominioIdAndVisitanteIdAndSaidaIsNullOrderByEntradaDesc(Long condominioId, Long visitanteId);
}
