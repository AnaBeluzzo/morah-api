package br.com.morah.repository;

import br.com.morah.model.Bloco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Acesso ao banco para Bloco. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface BlocoRepository extends JpaRepository<Bloco, Long> {

    /** Garante que o bloco pertence ao condomínio informado. */
    Optional<Bloco> findByIdAndCondominioId(Long id, Long condominioId);
}
