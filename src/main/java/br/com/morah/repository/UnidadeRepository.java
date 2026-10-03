package br.com.morah.repository;

import br.com.morah.model.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Acesso ao banco para Unidade. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface UnidadeRepository extends JpaRepository<Unidade, Long> {

    /** "BlocoCondominioId" = unidade.bloco.condominio.id: a unidade pertence ao condomínio informado? */
    Optional<Unidade> findByIdAndBlocoCondominioId(Long id, Long condominioId);
}
