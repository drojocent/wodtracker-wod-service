package com.wodtracker.wodservice.exception;

import com.wodtracker.wodservice.dto.ErrorResponse;
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), null);
    }

    @ExceptionHandler({AccessDeniedBusinessException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidStateException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid state", ex.getMessage(), null);
    }

    @ExceptionHandler(DuplicateWodDateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateWodDate(DuplicateWodDateException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Duplicate WOD date", ex.getMessage(), null);
    }

    @ExceptionHandler(DuplicateResultException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResult(DuplicateResultException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Duplicate result", ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", "Request validation failed", validationErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", ex.getMessage(), null);
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
