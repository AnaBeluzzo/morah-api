package br.com.morah.repository;

import br.com.morah.model.AreaComum;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para AreaComum. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface AreaComumRepository extends JpaRepository<AreaComum, Long> {
}
