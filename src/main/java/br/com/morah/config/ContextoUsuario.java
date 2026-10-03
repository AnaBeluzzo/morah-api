package br.com.morah.config;

import br.com.morah.exception.NaoAutenticadoException;
import br.com.morah.model.enums.TipoPerfil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * "Quem está chamando a API e em qual contexto".
 *
 * *** PROVISÓRIO ***  Na versão final, estes dados virão das claims do JWT emitido pelo Keycloak
 * (condominio_ativo, perfil_ativo...). Por enquanto são lidos de headers HTTP de teste:
 *   X-Condominio-Id, X-Pessoa-Id, X-Unidade-Id (opcional), X-Perfil.
 * Quando o Keycloak entrar, só a forma de montar este objeto muda; o resto do código
 * (controllers e services) continua recebendo um ContextoUsuario igual.
 *
 * @param unidadeId só existe para morador/proprietário; pode ser null.
 */
public record ContextoUsuario(Long condominioId, Long pessoaId, Long unidadeId, TipoPerfil perfil) {

    public static final String H_CONDOMINIO = "X-Condominio-Id";
    public static final String H_PESSOA = "X-Pessoa-Id";
    public static final String H_UNIDADE = "X-Unidade-Id";
    public static final String H_PERFIL = "X-Perfil";

    /** Monta o contexto a partir dos headers. Faltou algo obrigatório -> 401. */
    public static ContextoUsuario deRequisicao(HttpServletRequest request) {
        Long condominio = lerLong(request, H_CONDOMINIO, true);
        Long pessoa = lerLong(request, H_PESSOA, true);
        Long unidade = lerLong(request, H_UNIDADE, false);
        String perfilTexto = request.getHeader(H_PERFIL);
        if (perfilTexto == null || perfilTexto.isBlank()) {
            throw new NaoAutenticadoException("Header " + H_PERFIL + " ausente.");
        }
        try {
            return new ContextoUsuario(condominio, pessoa, unidade, TipoPerfil.valueOf(perfilTexto.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new NaoAutenticadoException("Perfil inválido em " + H_PERFIL + ": " + perfilTexto);
        }
    }

    private static Long lerLong(HttpServletRequest request, String header, boolean obrigatorio) {
        String valor = request.getHeader(header);
        if (valor == null || valor.isBlank()) {
            if (obrigatorio) {
                throw new NaoAutenticadoException("Header " + header + " ausente.");
            }
            return null;
        }
        try {
            return Long.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            throw new NaoAutenticadoException("Header " + header + " deve ser um número.");
        }
    }

    /** Síndico e portaria enxergam todos os avisos do condomínio; moradores só os destinados a eles. */
    public boolean veTodosOsAvisos() {
        return perfil == TipoPerfil.SINDICO || perfil == TipoPerfil.PORTARIA;
    }
}
