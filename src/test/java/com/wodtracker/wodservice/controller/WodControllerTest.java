package com.wodtracker.wodservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.wodservice.dto.request.WodRequestDTO;
import com.wodtracker.wodservice.dto.response.WodResponseDTO;
import com.wodtracker.wodservice.entity.WodType;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.service.WodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WodController.class)
@AutoConfigureMockMvc(addFilters = false)
class WodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WodService wodService;

    @Test
    void shouldCreateWodSuccessfully() throws Exception {
        WodRequestDTO requestDTO = new WodRequestDTO("Murph", "For time", WodType.FOR_TIME, LocalDate.now(), true);
        WodResponseDTO responseDTO = new WodResponseDTO(1L, "Murph", "For time", WodType.FOR_TIME, LocalDate.now(), true);

        when(wodService.createWod(any(WodRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/wods")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Murph"))
                .andExpect(jsonPath("$.type").value("FOR_TIME"));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        WodRequestDTO invalid = new WodRequestDTO("", "", null, null, null);

        mockMvc.perform(post("/wods")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.validationErrors.name").value("El nombre es obligatorio."))
                .andExpect(jsonPath("$.validationErrors.description").value("La descripcion es obligatoria."))
                .andExpect(jsonPath("$.validationErrors.type").value("El tipo es obligatorio."))
                .andExpect(jsonPath("$.validationErrors.date").value("La fecha es obligatoria."))
                .andExpect(jsonPath("$.validationErrors.approved").value("El indicador de aprobacion es obligatorio."));
    }

    @Test
    void shouldListWods() throws Exception {
        when(wodService.getAllWods()).thenReturn(List.of(
                new WodResponseDTO(1L, "Murph", "For time", WodType.FOR_TIME, LocalDate.now(), true)
        ));

        mockMvc.perform(get("/wods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Murph"));
    }

    @Test
    void shouldReturnTodayWod() throws Exception {
        when(wodService.getTodayWod()).thenReturn(
                new WodResponseDTO(1L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true)
        );

        mockMvc.perform(get("/wods/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fran"));
    }

    @Test
    void shouldHandleNotFoundException() throws Exception {
        when(wodService.getWodById(99L)).thenThrow(new ResourceNotFoundException("No se ha encontrado el WOD solicitado"));

        mockMvc.perform(get("/wods/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.message").value("No se ha encontrado el WOD solicitado"));
    }

    @Test
    void shouldUpdateWod() throws Exception {
        WodRequestDTO requestDTO = new WodRequestDTO("Updated", "Updated desc", WodType.EMOM, LocalDate.now(), false);
        WodResponseDTO responseDTO = new WodResponseDTO(1L, "Updated", "Updated desc", WodType.EMOM, LocalDate.now(), false);

        when(wodService.updateWod(any(Long.class), any(WodRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/wods/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }
}
