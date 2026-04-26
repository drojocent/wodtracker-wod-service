package com.wodtracker.wodservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.wodservice.dto.BenchmarkRequestDTO;
import com.wodtracker.wodservice.dto.BenchmarkResponseDTO;
import com.wodtracker.wodservice.dto.BenchmarkResultRequestDTO;
import com.wodtracker.wodservice.dto.BenchmarkResultResponseDTO;
import com.wodtracker.wodservice.entity.BenchmarkType;
import com.wodtracker.wodservice.service.BenchmarkResultService;
import com.wodtracker.wodservice.service.BenchmarkService;
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

@WebMvcTest(BenchmarkController.class)
@AutoConfigureMockMvc(addFilters = false)
class BenchmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BenchmarkService benchmarkService;

    @MockitoBean
    private BenchmarkResultService benchmarkResultService;

    @Test
    void shouldListBenchmarks() throws Exception {
        when(benchmarkService.getAllBenchmarks()).thenReturn(List.of(
                new BenchmarkResponseDTO(1L, "Fran", "Workout", BenchmarkType.FOR_TIME, LocalDateTime.now())
        ));

        mockMvc.perform(get("/benchmarks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fran"));
    }

    @Test
    void shouldCreateBenchmarkSuccessfully() throws Exception {
        BenchmarkRequestDTO requestDTO = new BenchmarkRequestDTO("Fran", "Workout", BenchmarkType.FOR_TIME);
        BenchmarkResponseDTO responseDTO = new BenchmarkResponseDTO(1L, "Fran", "Workout", BenchmarkType.FOR_TIME, LocalDateTime.now());

        when(benchmarkService.createBenchmark(any(BenchmarkRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/benchmarks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.type").value("FOR_TIME"));
    }

    @Test
    void shouldCreateBenchmarkResultSuccessfully() throws Exception {
        BenchmarkResultRequestDTO requestDTO = new BenchmarkResultRequestDTO("03:45");
        BenchmarkResultResponseDTO responseDTO = new BenchmarkResultResponseDTO(1L, 10L, 7L, "03:45", LocalDateTime.now());

        when(benchmarkResultService.createResult(any(Long.class), any(BenchmarkResultRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/benchmarks/10/results")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.benchmarkId").value(10L))
                .andExpect(jsonPath("$.result").value("03:45"));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingInvalidBenchmarkResult() throws Exception {
        BenchmarkResultRequestDTO invalid = new BenchmarkResultRequestDTO("");

        mockMvc.perform(post("/benchmarks/10/results")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.result").value("El resultado es obligatorio."));
    }

    @Test
    void shouldReturnMyBenchmarkResults() throws Exception {
        when(benchmarkResultService.getMyResults(10L)).thenReturn(List.of(
                new BenchmarkResultResponseDTO(1L, 10L, 7L, "03:45", LocalDateTime.now())
        ));

        mockMvc.perform(get("/benchmarks/10/results/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(7L));
    }
}
