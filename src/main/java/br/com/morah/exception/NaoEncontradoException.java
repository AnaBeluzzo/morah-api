package br.com.morah.exception;

import org.springframework.http.HttpStatus;

/** 404 - o recurso pedido não existe (ou não é visível para este usuário). */
public class NaoEncontradoException extends MorahException {

    public NaoEncontradoException(String mensagem) {
        super(HttpStatus.NOT_FOUND, mensagem);
    }
}
