package br.com.morah.repository;

import br.com.morah.model.Votacao;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Votacao. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface VotacaoRepository extends JpaRepository<Votacao, Long> {
}
