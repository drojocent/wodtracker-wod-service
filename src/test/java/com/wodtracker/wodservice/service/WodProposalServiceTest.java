package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.WodProposalRequestDTO;
import com.wodtracker.wodservice.dto.WodProposalResponseDTO;
import com.wodtracker.wodservice.entity.ProposalStatus;
import com.wodtracker.wodservice.entity.WodProposal;
import com.wodtracker.wodservice.entity.WodType;
import com.wodtracker.wodservice.exception.InvalidStateException;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.WodProposalRepository;
import com.wodtracker.wodservice.repository.WodRepository;
import com.wodtracker.wodservice.security.AuthenticatedUser;
import com.wodtracker.wodservice.security.AuthenticatedUserProvider;
import com.wodtracker.wodservice.service.impl.WodProposalServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WodProposalServiceTest {

    @Mock
    private WodProposalRepository wodProposalRepository;

    @Mock
    private WodRepository wodRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private WodProposalServiceImpl wodProposalService;

    @Test
    void shouldCreateProposalForAuthenticatedUser() {
        WodProposalRequestDTO requestDTO = new WodProposalRequestDTO(" Cindy ", " 20 min amrap ", WodType.AMRAP);
        WodProposal saved = new WodProposal(1L, 7L, "Cindy", "20 min amrap", WodType.AMRAP, ProposalStatus.PENDING, LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(wodProposalRepository.save(any(WodProposal.class))).thenReturn(saved);

        WodProposalResponseDTO response = wodProposalService.createProposal(requestDTO);

        assertThat(response.getUserId()).isEqualTo(7L);
        assertThat(response.getStatus()).isEqualTo(ProposalStatus.PENDING);
        assertThat(response.getName()).isEqualTo("Cindy");
    }

    @Test
    void shouldReturnPendingProposals() {
        WodProposal proposal = new WodProposal(1L, 7L, "Cindy", "Workout", WodType.AMRAP, ProposalStatus.PENDING, LocalDateTime.now());
        when(wodProposalRepository.findByStatusOrderByCreatedAtAsc(ProposalStatus.PENDING)).thenReturn(List.of(proposal));

        List<WodProposalResponseDTO> responses = wodProposalService.getPendingProposals();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo(ProposalStatus.PENDING);
    }

    @Test
    void shouldApprovePendingProposal() {
        WodProposal proposal = new WodProposal(1L, 7L, "Cindy", "Workout", WodType.AMRAP, ProposalStatus.PENDING, LocalDateTime.now());
        WodProposal approved = new WodProposal(1L, 7L, "Cindy", "Workout", WodType.AMRAP, ProposalStatus.APPROVED, LocalDateTime.now());

        when(wodProposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(wodRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(wodProposalRepository.save(proposal)).thenReturn(approved);

        WodProposalResponseDTO response = wodProposalService.approveProposal(1L);

        assertThat(response.getStatus()).isEqualTo(ProposalStatus.APPROVED);
    }

    @Test
    void shouldRejectAlreadyResolvedProposal() {
        WodProposal proposal = new WodProposal(1L, 7L, "Cindy", "Workout", WodType.AMRAP, ProposalStatus.APPROVED, LocalDateTime.now());
        when(wodProposalRepository.findById(1L)).thenReturn(Optional.of(proposal));

        assertThatThrownBy(() -> wodProposalService.rejectProposal(1L))
                .isInstanceOf(InvalidStateException.class)
                .hasMessage("Proposal is already APPROVED");
    }

    @Test
    void shouldThrowWhenProposalDoesNotExist() {
        when(wodProposalRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wodProposalService.approveProposal(3L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Proposal not found with id: 3");
    }
}
