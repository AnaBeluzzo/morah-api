package br.com.morah.repository;

import br.com.morah.model.PlanoModulo;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para PlanoModulo. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface PlanoModuloRepository extends JpaRepository<PlanoModulo, Long> {
}
