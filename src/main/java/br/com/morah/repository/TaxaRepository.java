package br.com.morah.repository;

import br.com.morah.model.Taxa;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Taxa. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface TaxaRepository extends JpaRepository<Taxa, Long> {
}
