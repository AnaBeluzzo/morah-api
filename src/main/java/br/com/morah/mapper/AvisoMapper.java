package br.com.morah.mapper;

import br.com.morah.dto.AvisoResponse;
import br.com.morah.dto.PessoaResumo;
import br.com.morah.dto.PublicoAlvoDto;
import br.com.morah.model.Comunicado;
import org.springframework.stereotype.Component;

/**
 * Converte Entity -> DTO manualmente (simples e fácil de entender; sem bibliotecas mágicas).
 * Deve ser chamado DENTRO da transação do service, pois lê relacionamentos LAZY (autor, bloco, unidade).
 */
@Component
public class AvisoMapper {

    public AvisoResponse paraResposta(Comunicado c, boolean lido) {
        return new AvisoResponse(
                c.getId(),
                c.getTitulo(),
                c.getConteudo(),
                c.getPrioridade(),
                c.getCategoria(),
                new PublicoAlvoDto(
                        c.getPublicoAlvoTipo(),
                        c.getBloco() != null ? c.getBloco().getId() : null,
                        c.getUnidade() != null ? c.getUnidade().getId() : null),
                new PessoaResumo(c.getAutor().getId(), c.getAutor().getNome(), c.getAutor().getTelefone()),
                c.getPublicadoEm(),
                c.getExpiraEm(),
                lido);
    }
}
