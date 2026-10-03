package br.com.morah.config;

import br.com.morah.exception.AcessoNegadoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * Um "porteiro" que roda antes de cada controller: se o método tem @PerfisPermitidos,
 * confere se o perfil do header X-Perfil está na lista. Senão, 403.
 * As exceções lançadas aqui também são tratadas pelo GlobalExceptionHandler.
 */
public class PerfisInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod metodo) {
            PerfisPermitidos permitidos = metodo.getMethodAnnotation(PerfisPermitidos.class);
            if (permitidos != null) {
                ContextoUsuario contexto = ContextoUsuario.deRequisicao(request); // 401 se faltar header
                if (!Arrays.asList(permitidos.value()).contains(contexto.perfil())) {
                    throw new AcessoNegadoException(
                            "O perfil '" + contexto.perfil().getValor() + "' não tem permissão para esta operação.");
                }
            }
        }
        return true;
    }
}
