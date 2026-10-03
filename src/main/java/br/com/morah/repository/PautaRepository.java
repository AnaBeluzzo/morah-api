package br.com.morah.repository;

import br.com.morah.model.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Pauta. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface PautaRepository extends JpaRepository<Pauta, Long> {
}
