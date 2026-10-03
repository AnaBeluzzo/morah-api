package br.com.morah.exception;

import org.springframework.http.HttpStatus;

/** 422 - a requisição está bem formada, mas viola uma regra de negócio. */
public class RegraNegocioException extends MorahException {

    public RegraNegocioException(String mensagem) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, mensagem);
    }
}
