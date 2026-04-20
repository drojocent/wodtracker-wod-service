package com.wodtracker.wodservice.exception;

import com.wodtracker.wodservice.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleDuplicateWodDate() {
        ResponseEntity<ErrorResponse> response = handler.handleDuplicateWodDate(
                new DuplicateWodDateException("Ya existe un WOD para la fecha 2026-04-20.")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Duplicate WOD date");
        assertThat(response.getBody().getMessage()).isEqualTo("Ya existe un WOD para la fecha 2026-04-20.");
    }

    @Test
    void shouldHandleDuplicateResult() {
        ResponseEntity<ErrorResponse> response = handler.handleDuplicateResult(
                new DuplicateResultException("Ya existe un resultado guardado para este WOD. Edita el existente.")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Duplicate result");
    }

    @Test
    void shouldHandleValidationErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "wodId", "Wod id is required"));
        bindingResult.addError(new FieldError("request", "result", "Result is required"));

        Method method = SampleController.class.getDeclaredMethod("sampleMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Validation failed");
        assertThat(response.getBody().getValidationErrors())
                .containsEntry("wodId", "Wod id is required")
                .containsEntry("result", "Result is required");
    }

    @Test
    void shouldHandleAccessDeniedException() {
        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(
                new AccessDeniedException("Forbidden")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Access denied");
        assertThat(response.getBody().getMessage()).isEqualTo("Forbidden");
    }

    @Test
    void shouldHandleUnexpectedException() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneral(
                new IllegalStateException("Boom")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Internal server error");
        assertThat(response.getBody().getMessage()).isEqualTo("Boom");
    }

    static class SampleController {
        @SuppressWarnings("unused")
        public void sampleMethod(String body) {
        }
    }
}
