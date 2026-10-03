package br.com.morah.repository;

import br.com.morah.model.Assinatura;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Assinatura. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {
}
