package br.com.morah.repository;

import br.com.morah.model.Pessoa;
import br.com.morah.model.VinculoUnidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface VinculoUnidadeRepository extends JpaRepository<VinculoUnidade, Long> {

    /** Pessoas com vínculo vigente (sem data fim, ou fim hoje/futuro) com a unidade: quem deve ser notificado. */
    @Query("select v.pessoa from VinculoUnidade v where v.unidade.id = :unidadeId and (v.fim is null or v.fim >= :hoje)")
    List<Pessoa> findPessoasVigentesDaUnidade(Long unidadeId, LocalDate hoje);
}
