package br.com.morah.repository;

import br.com.morah.model.Cobranca;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Cobranca. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {
}
