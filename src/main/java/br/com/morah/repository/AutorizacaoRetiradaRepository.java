package br.com.morah.repository;

import br.com.morah.model.AutorizacaoRetirada;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para AutorizacaoRetirada. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface AutorizacaoRetiradaRepository extends JpaRepository<AutorizacaoRetirada, Long> {
}
