package br.com.morah.repository;

import br.com.morah.model.Encomenda;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Encomenda. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface EncomendaRepository extends JpaRepository<Encomenda, Long> {
}
