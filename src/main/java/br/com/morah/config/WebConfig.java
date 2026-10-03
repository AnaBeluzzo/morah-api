package br.com.morah.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/** Liga as peças de infraestrutura do Spring MVC: interceptor de perfis, resolver de contexto, CORS e conversores. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PerfisInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ContextoUsuarioResolver());
    }

    /** CORS liberado para desenvolvimento (o app React roda em outra porta). Restringir em produção. */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS");
    }

    /**
     * Permite usar ?prioridade=urgente (minúsculo, como no YAML) em parâmetros de URL.
     * Sem isso, o Spring só aceitaria o nome exato da constante: ?prioridade=URGENTE.
     * Valor inexistente -> IllegalArgumentException -> o Spring responde 400 sozinho.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new EnumIgnoraCaixa());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static class EnumIgnoraCaixa implements ConverterFactory<String, Enum> {
        @Override
        public <T extends Enum> Converter<String, T> getConverter(Class<T> tipo) {
            return texto -> (T) Enum.valueOf(tipo, texto.trim().toUpperCase());
        }
    }
}
