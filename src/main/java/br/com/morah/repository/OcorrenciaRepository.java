package br.com.morah.repository;

import br.com.morah.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Ocorrencia. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
}
