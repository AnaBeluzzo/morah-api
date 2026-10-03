package br.com.morah.dto;

import java.util.List;

/** Schema AutorizacaoVisitaPage do YAML. */
public record AutorizacaoVisitaPage(List<AutorizacaoVisitaResponse> content, PageMetadata page) {
}
