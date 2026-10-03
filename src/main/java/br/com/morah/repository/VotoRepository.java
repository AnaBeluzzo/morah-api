package br.com.morah.repository;

import br.com.morah.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Voto. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface VotoRepository extends JpaRepository<Voto, Long> {
}
