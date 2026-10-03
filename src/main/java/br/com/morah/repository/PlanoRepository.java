package br.com.morah.repository;

import br.com.morah.model.Plano;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Plano. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface PlanoRepository extends JpaRepository<Plano, Long> {
}
