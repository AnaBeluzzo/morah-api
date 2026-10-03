package br.com.morah.config;

import br.com.morah.model.enums.TipoPerfil;

import java.lang.annotation.*;

/**
 * Anotação para marcar quais perfis podem chamar um endpoint, ex.: @PerfisPermitidos(TipoPerfil.SINDICO).
 * Será verificada pelo PerfisInterceptor ANTES de o controller executar.
 * (Com o Keycloak isto será substituído por @PreAuthorize / scopes do token.)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PerfisPermitidos {
    TipoPerfil[] value();
}
