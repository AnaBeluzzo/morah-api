package br.com.morah.repository;

import br.com.morah.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Reserva. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
