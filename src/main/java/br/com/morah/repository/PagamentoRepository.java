package br.com.morah.repository;

import br.com.morah.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Pagamento. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
