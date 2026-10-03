package br.com.morah.exception;

import org.springframework.http.HttpStatus;

/** 401 - faltam os dados de identificação (headers simulados agora; JWT do Keycloak depois). */
public class NaoAutenticadoException extends MorahException {

    public NaoAutenticadoException(String mensagem) {
        super(HttpStatus.UNAUTHORIZED, mensagem);
    }
}
