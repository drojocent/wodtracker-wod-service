package com.wodtracker.wodservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.wodservice.dto.PersonalRecordRequestDTO;
import com.wodtracker.wodservice.dto.PersonalRecordResponseDTO;
import com.wodtracker.wodservice.entity.ExerciseType;
import com.wodtracker.wodservice.service.PersonalRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonalRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
class PersonalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonalRecordService personalRecordService;

    @Test
    void shouldListExercises() throws Exception {
        when(personalRecordService.getExercises()).thenReturn(List.of(ExerciseType.BACK_SQUAT, ExerciseType.SNATCH));

        mockMvc.perform(get("/prs/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("BACK_SQUAT"))
                .andExpect(jsonPath("$[1]").value("SNATCH"));
    }

    @Test
    void shouldReturnCurrentPersonalRecord() throws Exception {
        when(personalRecordService.getMyPersonalRecord(ExerciseType.BACK_SQUAT))
                .thenReturn(new PersonalRecordResponseDTO(
                        1L,
                        7L,
                        ExerciseType.BACK_SQUAT,
                        new BigDecimal("145.00"),
                        LocalDateTime.now()
                ));

        mockMvc.perform(get("/prs/BACK_SQUAT/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exercise").value("BACK_SQUAT"))
                .andExpect(jsonPath("$.weight").value(145.00));
    }

    @Test
    void shouldCreatePersonalRecord() throws Exception {
        PersonalRecordRequestDTO requestDTO = new PersonalRecordRequestDTO(new BigDecimal("92.50"));
        when(personalRecordService.createPersonalRecord(any(ExerciseType.class), any(PersonalRecordRequestDTO.class)))
                .thenReturn(new PersonalRecordResponseDTO(
                        1L,
                        7L,
                        ExerciseType.BENCH_PRESS,
                        new BigDecimal("92.50"),
                        LocalDateTime.now()
                ));

        mockMvc.perform(post("/prs/BENCH_PRESS")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exercise").value("BENCH_PRESS"))
                .andExpect(jsonPath("$.weight").value(92.50));
    }

    @Test
    void shouldReturnBadRequestWhenWeightIsMissing() throws Exception {
        PersonalRecordRequestDTO invalid = new PersonalRecordRequestDTO(null);

        mockMvc.perform(post("/prs/SNATCH")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.weight").value("Weight is required"));
    }

    @Test
    void shouldReturnHistory() throws Exception {
        when(personalRecordService.getMyHistory(ExerciseType.DEADLIFT)).thenReturn(List.of(
                new PersonalRecordResponseDTO(
                        1L,
                        7L,
                        ExerciseType.DEADLIFT,
                        new BigDecimal("180.00"),
                        LocalDateTime.now()
                )
        ));

        mockMvc.perform(get("/prs/DEADLIFT/me/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exercise").value("DEADLIFT"))
                .andExpect(jsonPath("$[0].userId").value(7L));
    }
}
