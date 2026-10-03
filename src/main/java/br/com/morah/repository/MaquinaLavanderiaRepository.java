package br.com.morah.repository;

import br.com.morah.model.MaquinaLavanderia;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para MaquinaLavanderia. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface MaquinaLavanderiaRepository extends JpaRepository<MaquinaLavanderia, Long> {
}
