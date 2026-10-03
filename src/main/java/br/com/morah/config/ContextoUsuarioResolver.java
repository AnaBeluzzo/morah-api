package br.com.morah.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Faz o Spring entregar um objeto ContextoUsuario como parâmetro dos métodos do controller:
 * basta declarar "ContextoUsuario contexto" na assinatura.
 */
public class ContextoUsuarioResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ContextoUsuario.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return ContextoUsuario.deRequisicao(webRequest.getNativeRequest(HttpServletRequest.class));
    }
}
