package br.com.morah.repository;

import br.com.morah.model.Assembleia;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Assembleia. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface AssembleiaRepository extends JpaRepository<Assembleia, Long> {
}
