package br.com.morah.repository;

import br.com.morah.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Modulo. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
}
