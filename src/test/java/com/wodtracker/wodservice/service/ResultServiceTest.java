package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.ResultRequestDTO;
import com.wodtracker.wodservice.dto.ResultResponseDTO;
import com.wodtracker.wodservice.entity.Result;
import com.wodtracker.wodservice.entity.Wod;
import com.wodtracker.wodservice.entity.WodType;
import com.wodtracker.wodservice.exception.AccessDeniedBusinessException;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.ResultRepository;
import com.wodtracker.wodservice.repository.WodRepository;
import com.wodtracker.wodservice.security.AuthenticatedUser;
import com.wodtracker.wodservice.security.AuthenticatedUserProvider;
import com.wodtracker.wodservice.service.impl.ResultServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private WodRepository wodRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private ResultServiceImpl resultService;

    @Test
    void shouldCreateResultForAuthenticatedUser() {
        Wod wod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        ResultRequestDTO requestDTO = new ResultRequestDTO(10L, " 03:45 ");
        Result savedResult = new Result(1L, 7L, wod, "03:45", LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(wodRepository.findById(10L)).thenReturn(Optional.of(wod));
        when(resultRepository.save(any(Result.class))).thenReturn(savedResult);

        ResultResponseDTO response = resultService.createResult(requestDTO);

        ArgumentCaptor<Result> resultCaptor = ArgumentCaptor.forClass(Result.class);
        verify(resultRepository).save(resultCaptor.capture());
        assertThat(resultCaptor.getValue().getUserId()).isEqualTo(7L);
        assertThat(resultCaptor.getValue().getResult()).isEqualTo("03:45");
        assertThat(response.getWodName()).isEqualTo("Fran");
    }

    @Test
    void shouldThrowWhenCreatingResultForUnknownWod() {
        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(wodRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultService.createResult(new ResultRequestDTO(10L, "03:45")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("WOD not found with id: 10");
    }

    @Test
    void shouldAllowUserToReadOwnResults() {
        Wod wod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Result result = new Result(1L, 7L, wod, "03:45", LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(resultRepository.findByUserIdOrderByCreatedAtDesc(7L)).thenReturn(List.of(result));

        List<ResultResponseDTO> responses = resultService.getResultsByUserId(7L);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getUserId()).isEqualTo(7L);
    }

    @Test
    void shouldRejectAccessToAnotherUsersResultsForNonAdmin() {
        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));

        assertThatThrownBy(() -> resultService.getResultsByUserId(9L))
                .isInstanceOf(AccessDeniedBusinessException.class)
                .hasMessage("You can only access your own results");
    }

    @Test
    void shouldAllowAdminToReadAnyUsersResults() {
        Wod wod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Result result = new Result(1L, 9L, wod, "03:45", LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(1L, "admin@example.com", Set.of("ROLE_ADMIN")));
        when(resultRepository.findByUserIdOrderByCreatedAtDesc(9L)).thenReturn(List.of(result));

        List<ResultResponseDTO> responses = resultService.getResultsByUserId(9L);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getUserId()).isEqualTo(9L);
    }
}
