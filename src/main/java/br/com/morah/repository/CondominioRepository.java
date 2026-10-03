package br.com.morah.repository;

import br.com.morah.model.Condominio;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Condominio. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface CondominioRepository extends JpaRepository<Condominio, Long> {
}
