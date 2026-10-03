package br.com.morah.dto;

import java.util.List;

/** Schema AcessoPage do YAML. */
public record AcessoPage(List<AcessoResponse> content, PageMetadata page) {
}
