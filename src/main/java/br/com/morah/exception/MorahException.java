package br.com.morah.exception;

import org.springframework.http.HttpStatus;

/**
 * Mãe de todas as exceções de negócio do Morah.
 * Cada filha informa qual status HTTP representa; o GlobalExceptionHandler transforma isso em
 * uma resposta de erro padronizada (RFC 9457) sem precisar de um método por exceção.
 */
public abstract class MorahException extends RuntimeException {

    private final HttpStatus status;

    protected MorahException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
