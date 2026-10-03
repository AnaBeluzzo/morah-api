package br.com.morah.repository;

import br.com.morah.model.ReservaLavanderia;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para ReservaLavanderia. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface ReservaLavanderiaRepository extends JpaRepository<ReservaLavanderia, Long> {
}
