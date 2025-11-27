package dev.andre.ResumeAiAnalysis.ExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import dev.andre.ResumeAiAnalysis.Auth.Exceptions.EmailAlreadyExist;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ============================================================================================
    //  AUTH EXCEPTIONS
    // ============================================================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionsResponse> handleBadCredentials(BadCredentialsException ex, WebRequest req) {
        ExceptionsResponse body = new ExceptionsResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), OffsetDateTime.now(), req.getDescription(false));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(EmailAlreadyExist.class)
    public ResponseEntity<ExceptionsResponse> handleEmailAlreadyExists(EmailAlreadyExist ex, WebRequest req) {
        ExceptionsResponse body = new ExceptionsResponse(ex.getMessage(), HttpStatus.CONFLICT.value(), OffsetDateTime.now(), req.getDescription(false));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // ============================================================================================
    //  OVERRIDE - MethodArgumentNotValidException -> padroniza a resposta de validação (@Valid)
    // ============================================================================================
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        // Opções: pegar primeiro erro ou concatenar todos.
        // Aqui eu concateno todos em uma string amigável:
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ExceptionsResponse body = new ExceptionsResponse(errors, HttpStatus.BAD_REQUEST.value(), OffsetDateTime.now(), request.getDescription(false));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============================================================================================
    //  HttpMessageNotReadableException -> JSON mal formatado / tipo errado
    // ============================================================================================
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String message = "JSON inválido ou mal formatado";

        if (ex.getCause() instanceof InvalidFormatException ife) {
            String field = ife.getPath().isEmpty() ? "campo desconhecido" : ife.getPath().get(0).getFieldName();
            message = "Tipo de dado inválido: campo '" + field + "' recebeu valor '" + ife.getValue() + "'";
        }

        ExceptionsResponse body = new ExceptionsResponse(message, HttpStatus.BAD_REQUEST.value(), OffsetDateTime.now(), request.getDescription(false));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============================================================================================
    //  MethodArgumentTypeMismatchException -> tipo errado em @PathVariable ou @RequestParam
    // ============================================================================================
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionsResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest req) {
        ExceptionsResponse body = new ExceptionsResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), OffsetDateTime.now(), req.getDescription(false));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============================================================================================
    //  GENERIC FALLBACK
    // ============================================================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionsResponse> handleGeneric(Exception ex, WebRequest req) {
        ExceptionsResponse body = new ExceptionsResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), OffsetDateTime.now(), req.getDescription(false));
        // opcional: log.error("Erro interno", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
