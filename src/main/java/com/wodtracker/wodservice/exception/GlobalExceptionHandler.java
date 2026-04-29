package com.wodtracker.wodservice.exception;

import com.wodtracker.wodservice.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_RECURSO_NO_ENCONTRADO = "Recurso no encontrado";
    private static final String ERROR_ACCESO_DENEGADO = "Acceso denegado";
    private static final String ERROR_ESTADO_NO_VALIDO = "Estado no válido";
    private static final String ERROR_FECHA_WOD_DUPLICADA = "Fecha de WOD duplicada";
    private static final String ERROR_RESULTADO_DUPLICADO = "Resultado duplicado";
    private static final String ERROR_VALIDACION = "Validación fallida";
    private static final String ERROR_INTERNO = "Error interno del servidor";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ERROR_RECURSO_NO_ENCONTRADO, ex.getMessage(), null);
    }

    @ExceptionHandler({AccessDeniedBusinessException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                ERROR_ACCESO_DENEGADO,
                resolveAccessDeniedMessage(ex),
                null
        );
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidStateException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ERROR_ESTADO_NO_VALIDO, ex.getMessage(), null);
    }

    @ExceptionHandler(DuplicateWodDateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateWodDate(DuplicateWodDateException ex) {
        return buildResponse(HttpStatus.CONFLICT, ERROR_FECHA_WOD_DUPLICADA, ex.getMessage(), null);
    }

    @ExceptionHandler(DuplicateResultException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResult(DuplicateResultException ex) {
        return buildResponse(HttpStatus.CONFLICT, ERROR_RESULTADO_DUPLICADO, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ERROR_VALIDACION,
                "La solicitud contiene errores de validación.",
                validationErrors
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ERROR_INTERNO,
                "Ha ocurrido un error interno del servidor.",
                null
        );
    }

    private String resolveAccessDeniedMessage(Exception ex) {
        if (ex instanceof AccessDeniedBusinessException) {
            return ex.getMessage();
        }
        return "No tienes permisos para acceder a este recurso.";
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String error,
            String message,
            Map<String, String> validationErrors
    ) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(error, message, LocalDateTime.now(), validationErrors)
        );
    }
}
