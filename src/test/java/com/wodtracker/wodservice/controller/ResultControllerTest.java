package com.wodtracker.wodservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.wodservice.dto.ResultRequestDTO;
import com.wodtracker.wodservice.dto.ResultResponseDTO;
import com.wodtracker.wodservice.exception.AccessDeniedBusinessException;
import com.wodtracker.wodservice.service.ResultService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResultController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ResultService resultService;

    @Test
    void shouldCreateResultSuccessfully() throws Exception {
        ResultRequestDTO requestDTO = new ResultRequestDTO(10L, "03:45");
        ResultResponseDTO responseDTO = new ResultResponseDTO(1L, 7L, 10L, "Fran", "21-15-9 thrusters", "03:45", LocalDateTime.now());

        when(resultService.createResult(any(ResultRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/results")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wodId").value(10L))
                .andExpect(jsonPath("$.result").value("03:45"));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingInvalidResult() throws Exception {
        ResultRequestDTO invalid = new ResultRequestDTO(null, "");

        mockMvc.perform(post("/results")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.wodId").value("El identificador del WOD es obligatorio."))
                .andExpect(jsonPath("$.validationErrors.result").value("El resultado es obligatorio."));
    }

    @Test
    void shouldReturnResultsByUserId() throws Exception {
        when(resultService.getResultsByUserId(7L)).thenReturn(List.of(
                new ResultResponseDTO(1L, 7L, 10L, "Fran", "21-15-9 thrusters", "03:45", LocalDateTime.now())
        ));

        mockMvc.perform(get("/results/user/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(7L));
    }

    @Test
    void shouldHandleForbiddenBusinessException() throws Exception {
        when(resultService.getResultsByUserId(9L))
                .thenThrow(new AccessDeniedBusinessException("Solo puedes consultar tus propios resultados."));

        mockMvc.perform(get("/results/user/9"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Acceso denegado"))
                .andExpect(jsonPath("$.message").value("Solo puedes consultar tus propios resultados."));
    }
}
