package br.com.morah.dto;

import java.util.List;

/** Schema AvisoPage do YAML: { content: [...], page: {...} }. */
public record AvisoPage(List<AvisoResponse> content, PageMetadata page) {
}
