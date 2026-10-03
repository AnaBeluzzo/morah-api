package br.com.morah.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configura o Swagger UI (http://localhost:8080/v1/swagger-ui.html).
 * Registramos os 4 headers de teste como "esquemas de segurança" para o botão Authorize do Swagger
 * preencher automaticamente. Será trocado por um esquema Bearer/OAuth2 quando o Keycloak entrar.
 */
@Configuration
public class OpenApiConfig {

    private static final List<String> HEADERS = List.of(
            ContextoUsuario.H_CONDOMINIO, ContextoUsuario.H_PESSOA, ContextoUsuario.H_UNIDADE, ContextoUsuario.H_PERFIL);

    @Bean
    public OpenAPI morahOpenAPI() {
        Components componentes = new Components();
        SecurityRequirement requisito = new SecurityRequirement();
        for (String header : HEADERS) {
            componentes.addSecuritySchemes(header, new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name(header)
                    .description("Header de teste (simula o JWT do Keycloak)."));
            requisito.addList(header);
        }
        return new OpenAPI()
                .info(new Info().title("Morah API").version("1.0.0")
                        .description("Backend do Morah. Autenticação SIMULADA por headers X-*."))
                .components(componentes)
                .addSecurityItem(requisito);
    }
}
