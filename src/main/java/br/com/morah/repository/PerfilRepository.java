package br.com.morah.repository;

import br.com.morah.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Perfil. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface PerfilRepository extends JpaRepository<Perfil, Long> {
}
