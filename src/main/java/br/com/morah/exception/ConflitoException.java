package br.com.morah.exception;

import org.springframework.http.HttpStatus;

/** 409 - conflito com o estado atual (ex.: horário já reservado). Será usado pelos próximos módulos. */
public class ConflitoException extends MorahException {

    public ConflitoException(String mensagem) {
        super(HttpStatus.CONFLICT, mensagem);
    }
}
