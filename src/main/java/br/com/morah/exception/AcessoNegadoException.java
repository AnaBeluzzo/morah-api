package br.com.morah.exception;

import org.springframework.http.HttpStatus;

/** 403 - o perfil do usuário não tem permissão para esta operação. */
public class AcessoNegadoException extends MorahException {

    public AcessoNegadoException(String mensagem) {
        super(HttpStatus.FORBIDDEN, mensagem);
    }
}
