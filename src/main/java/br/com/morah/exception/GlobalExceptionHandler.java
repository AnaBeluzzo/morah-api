package br.com.morah.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Captura as exceções lançadas por qualquer controller/service e devolve um ProblemDetail
 * (formato padrão RFC 9457, content-type application/problem+json) em vez de um stack trace.
 *
 * Estende ResponseEntityExceptionHandler, que já trata os erros "de infraestrutura" do Spring MVC
 * (JSON malformado, método HTTP errado, parâmetro com tipo inválido...) também como ProblemDetail.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 401, 403, 404, 409, 410, 422: tudo que herda de MorahException. */
    @ExceptionHandler(MorahException.class)
    public ProblemDetail tratarNegocio(MorahException ex, HttpServletRequest request) {
        return problema(ex.getStatus(), ex.getMessage(), request.getRequestURI());
    }

    /** Sort inválido (?sort=campoInexistente) vira 400 em vez de 500. */
    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail tratarOrdenacaoInvalida(PropertyReferenceException ex, HttpServletRequest request) {
        return problema(HttpStatus.BAD_REQUEST, "Campo de ordenação inválido: " + ex.getPropertyName(), request.getRequestURI());
    }

    /** Violação de constraint do banco (ex.: CPF duplicado, REQ017). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail tratarIntegridade(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Violação de integridade: {}", ex.getMostSpecificCause().getMessage());
        return problema(HttpStatus.CONFLICT, "A operação viola uma restrição de integridade dos dados.", request.getRequestURI());
    }

    /** Rede de segurança: qualquer erro inesperado vira 500 SEM vazar detalhes internos. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarInesperado(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado em {}", request.getRequestURI(), ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado.", request.getRequestURI());
    }

    /** Bean Validation falhou (@NotBlank, @Size...): 400 com a lista "erros" [{campo, mensagem}] do YAML. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        List<Map<String, String>> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(this::paraMapa)
                .toList();
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Um ou mais campos são inválidos.");
        pd.setTitle("Requisição inválida");
        pd.setProperty("erros", erros);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(pd);
    }

    private Map<String, String> paraMapa(FieldError e) {
        return Map.of("campo", e.getField(), "mensagem", String.valueOf(e.getDefaultMessage()));
    }

    private ProblemDetail problema(HttpStatus status, String detalhe, String caminho) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalhe);
        pd.setTitle(status.getReasonPhrase());
        pd.setInstance(URI.create(caminho));
        return pd;
    }
}
