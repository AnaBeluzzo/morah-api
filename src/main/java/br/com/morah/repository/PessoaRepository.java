package br.com.morah.repository;

import br.com.morah.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Pessoa. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
}
