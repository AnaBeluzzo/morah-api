package br.com.morah.repository;

import br.com.morah.model.DocumentoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para DocumentoFinanceiro. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface DocumentoFinanceiroRepository extends JpaRepository<DocumentoFinanceiro, Long> {
}
