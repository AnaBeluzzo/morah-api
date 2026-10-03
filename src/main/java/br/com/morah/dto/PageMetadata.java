package br.com.morah.dto;

import org.springframework.data.domain.Page;

/** Schema PageMetadata do YAML: o bloco "page" de toda resposta paginada. Reutilizável pelos outros módulos. */
public record PageMetadata(int page, int size, long totalElements, int totalPages) {

    public static PageMetadata de(Page<?> pagina) {
        return new PageMetadata(pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
    }
}
