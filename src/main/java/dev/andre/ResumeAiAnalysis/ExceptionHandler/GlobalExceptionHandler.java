package dev.andre.ResumeAiAnalysis.ExceptionHandler;

import dev.andre.ResumeAiAnalysis.Auth.Exceptions.EmailAlreadyExist;
import dev.andre.ResumeAiAnalysis.Auth.Exceptions.UnauthenticatedUser;
import dev.andre.ResumeAiAnalysis.Vacancy.Exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
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
    public ResponseEntity<ExceptionsResponse> handleBadCredentials(
            BadCredentialsException ex,
            WebRequest request
    ) {
        ExceptionsResponse body = new ExceptionsResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(EmailAlreadyExist.class)
    public ResponseEntity<ExceptionsResponse> handleEmailAlreadyExists(
            EmailAlreadyExist ex,
            WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ExceptionsResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        ));
    }

    @ExceptionHandler(UnauthenticatedUser.class)
    public ResponseEntity<ExceptionsResponse> handleUnauthenticateUser(
            UnauthenticatedUser ex,
            WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ExceptionsResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        ));
    }



    // ============================================================================================
    //  @Valid (Bean Validation)
    // ============================================================================================

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionsResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest req) {
        ex.printStackTrace(); // <-- ESSENCIAL
        String errors = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));

        ExceptionsResponse body = new ExceptionsResponse(
                errors,
                HttpStatus.BAD_REQUEST.value(),
                OffsetDateTime.now(),
                req.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============================================================================================
    //  JSON MAL FORMATADO / TIPO ERRADO
    // ============================================================================================

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String message = "JSON inválido ou tipos incorretos. Verifique os campos enviados.";

        ExceptionsResponse body = new ExceptionsResponse(
                message,
                HttpStatus.BAD_REQUEST.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    // ============================================================================================
    //  PATHVARIABLE / REQUESTPARAM com tipo errado
    // ============================================================================================

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionsResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request
    ) {
        String message = "Valor inválido para o parâmetro '" + ex.getName() + "'";

        ExceptionsResponse body = new ExceptionsResponse(
                message,
                HttpStatus.BAD_REQUEST.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ============================================================================================
    //  Vacancy Entity erros
    // ============================================================================================

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionsResponse> handleConflictException(
            ConflictException ex,
            WebRequest request
    ) {
        ExceptionsResponse body = new ExceptionsResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(UserCannotAccessOrDoThat.class)
    public ResponseEntity<ExceptionsResponse> handleUserCannotAccessOrDoThat(
            UserCannotAccessOrDoThat  ex,
            WebRequest request
    ) {
        ExceptionsResponse body = new ExceptionsResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(FileEmptyException.class)
    public ResponseEntity<ExceptionsResponse> handleFileEmptyException(
            FileEmptyException ex,
            WebRequest request
    ) {
        ExceptionsResponse body = new ExceptionsResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ExceptionsResponse> handleFileTooLargeException(
            FileTooLargeException ex,
            WebRequest request
    ) {
        ExceptionsResponse body = new ExceptionsResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<ExceptionsResponse> handleInvalidFileTypeException(
            InvalidFileTypeException ex,
            WebRequest request
    ) {
        ExceptionsResponse body = new ExceptionsResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    // ============================================================================================
    //  Erros Gerais
    // ============================================================================================
    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<ExceptionsResponse> handleInternalServerError(
            InternalServerError ex,
            WebRequest request
    ) {
        ExceptionsResponse body = new ExceptionsResponse(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionsResponse> handleNotFoundException(
            NotFoundException ex,
            WebRequest request
    ) {
        ExceptionsResponse body = new ExceptionsResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                OffsetDateTime.now(),
                request.getDescription(false)
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // ============================================================================================
    //  FALLBACK — qualquer exception não tratada
    // ============================================================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionsResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        ExceptionsResponse response = new ExceptionsResponse(
                "Ocorreu um erro interno. Tente novamente mais tarde.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                OffsetDateTime.now(),
                request.getRequestURI()
        );

        // Log real do erro (importante)
        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
