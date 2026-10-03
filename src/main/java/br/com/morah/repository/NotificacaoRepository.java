package br.com.morah.repository;

import br.com.morah.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso ao banco para Notificacao. O Spring cria a implementação (save, findById, findAll, delete...) sozinho. */
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
}
