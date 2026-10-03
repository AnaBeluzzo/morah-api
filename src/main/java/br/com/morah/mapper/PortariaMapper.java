package br.com.morah.mapper;

import br.com.morah.dto.AcessoResponse;
import br.com.morah.dto.AutorizacaoVisitaResponse;
import br.com.morah.dto.VisitanteResponse;
import br.com.morah.model.Acesso;
import br.com.morah.model.AutorizacaoVisita;
import br.com.morah.model.Visitante;
import br.com.morah.model.enums.StatusAutorizacaoVisita;
import org.springframework.stereotype.Component;

/** Entity -> DTO do módulo Portaria. Chamar dentro da transação do service (lê relacionamentos LAZY). */
@Component
public class PortariaMapper {

    public VisitanteResponse paraResposta(Visitante v) {
        return new VisitanteResponse(v.getId(), v.getNome(), v.getDocumento(), v.getTelefone(), v.getObservacao());
    }

    /** O status vem de fora porque o "efetivo" pode ser EXPIRADA mesmo com PENDENTE gravado no banco. */
    public AutorizacaoVisitaResponse paraResposta(AutorizacaoVisita a, StatusAutorizacaoVisita statusEfetivo) {
        return new AutorizacaoVisitaResponse(
                a.getId(),
                paraResposta(a.getVisitante()),
                a.getUnidade().getId(),
                a.getMotivo(),
                a.getFotoUrl(),
                a.getSolicitadoEm(),
                a.getRespondidoEm(),
                statusEfetivo);
    }

    public AcessoResponse paraResposta(Acesso a) {
        return new AcessoResponse(
                a.getId(),
                a.getAutorizacao() != null ? a.getAutorizacao().getId() : null,
                paraResposta(a.getVisitante()),
                a.getEntrada(),
                a.getSaida(),
                a.getTipo(),
                a.getStatus(),
                a.getFotoUrl());
    }
}
