package br.com.morah.repository;

import br.com.morah.model.PessoaPerfil;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para PessoaPerfil. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface PessoaPerfilRepository extends JpaRepository<PessoaPerfil, Long> {
}
