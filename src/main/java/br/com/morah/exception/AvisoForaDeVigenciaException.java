package br.com.morah.exception;

import org.springframework.http.HttpStatus;

/** 410 - o aviso existiu, mas já expirou (fora de vigência). */
public class AvisoForaDeVigenciaException extends MorahException {

    public AvisoForaDeVigenciaException(String mensagem) {
        super(HttpStatus.GONE, mensagem);
    }
}
