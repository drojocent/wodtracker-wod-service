package com.wodtracker.wodservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.wodservice.dto.WodProposalRequestDTO;
import com.wodtracker.wodservice.dto.WodProposalResponseDTO;
import com.wodtracker.wodservice.entity.ProposalStatus;
import com.wodtracker.wodservice.entity.WodType;
import com.wodtracker.wodservice.exception.InvalidStateException;
import com.wodtracker.wodservice.service.WodProposalService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WodProposalController.class)
@AutoConfigureMockMvc(addFilters = false)
class WodProposalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WodProposalService wodProposalService;

    @Test
    void shouldCreateProposalSuccessfully() throws Exception {
        WodProposalRequestDTO requestDTO = new WodProposalRequestDTO("Cindy", "20 min amrap", WodType.AMRAP);
        WodProposalResponseDTO responseDTO = new WodProposalResponseDTO(
                1L, 7L, "Cindy", "20 min amrap", WodType.AMRAP, ProposalStatus.PENDING, LocalDateTime.now()
        );

        when(wodProposalService.createProposal(any(WodProposalRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/proposals")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldListPendingProposals() throws Exception {
        when(wodProposalService.getPendingProposals()).thenReturn(List.of(
                new WodProposalResponseDTO(1L, 7L, "Cindy", "20 min amrap", WodType.AMRAP, ProposalStatus.PENDING, LocalDateTime.now())
        ));

        mockMvc.perform(get("/proposals/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cindy"));
    }

    @Test
    void shouldApproveProposal() throws Exception {
        when(wodProposalService.approveProposal(1L)).thenReturn(
                new WodProposalResponseDTO(1L, 7L, "Cindy", "20 min amrap", WodType.AMRAP, ProposalStatus.APPROVED, LocalDateTime.now())
        );

        mockMvc.perform(patch("/proposals/1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldHandleInvalidStateException() throws Exception {
        when(wodProposalService.rejectProposal(1L))
                .thenThrow(new InvalidStateException("La propuesta ya esta en estado APPROVED."));

        mockMvc.perform(patch("/proposals/1/reject"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Estado no válido"))
                .andExpect(jsonPath("$.message").value("La propuesta ya esta en estado APPROVED."));
    }
}
