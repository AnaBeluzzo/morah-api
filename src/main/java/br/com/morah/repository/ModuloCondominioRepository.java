package br.com.morah.repository;

import br.com.morah.model.ModuloCondominio;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para ModuloCondominio. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface ModuloCondominioRepository extends JpaRepository<ModuloCondominio, Long> {
}
