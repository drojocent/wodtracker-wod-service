package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.request.ResultRequestDTO;
import com.wodtracker.wodservice.dto.response.ResultResponseDTO;
import com.wodtracker.wodservice.entity.Result;
import com.wodtracker.wodservice.entity.Wod;
import com.wodtracker.wodservice.entity.WodType;
import com.wodtracker.wodservice.exception.AccessDeniedBusinessException;
import com.wodtracker.wodservice.exception.DuplicateResultException;
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
import static org.mockito.ArgumentMatchers.eq;
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
        Result savedResult = new Result(1L, 7L, wod, "Fran", "Workout", "03:45", LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(wodRepository.findById(10L)).thenReturn(Optional.of(wod));
        when(resultRepository.save(any(Result.class))).thenReturn(savedResult);

        ResultResponseDTO response = resultService.createResult(requestDTO);

        ArgumentCaptor<Result> resultCaptor = ArgumentCaptor.forClass(Result.class);
        verify(resultRepository).save(resultCaptor.capture());
        assertThat(resultCaptor.getValue().getUserId()).isEqualTo(7L);
        assertThat(resultCaptor.getValue().getWodName()).isEqualTo("Fran");
        assertThat(resultCaptor.getValue().getWodDescription()).isEqualTo("Workout");
        assertThat(resultCaptor.getValue().getResult()).isEqualTo("03:45");
        assertThat(response.getWodName()).isEqualTo("Fran");
        assertThat(response.getWodDescription()).isEqualTo("Workout");
    }

    @Test
    void shouldThrowWhenCreatingResultForUnknownWod() {
        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(wodRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultService.createResult(new ResultRequestDTO(10L, "03:45")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se ha encontrado el WOD solicitado");
    }

    @Test
    void shouldRejectDuplicateResultCreationForSameUserAndWod() {
        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(resultRepository.existsByUserIdAndWodId(7L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> resultService.createResult(new ResultRequestDTO(10L, "03:45")))
                .isInstanceOf(DuplicateResultException.class)
                .hasMessage("Ya existe un resultado guardado para este WOD. Edita el existente.");
    }

    @Test
    void shouldAllowUserToReadOwnResults() {
        Wod wod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Result result = new Result(1L, 7L, wod, "Fran", "Workout", "03:45", LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(resultRepository.findByUserIdOrderByCreatedAtDesc(7L)).thenReturn(List.of(result));

        List<ResultResponseDTO> responses = resultService.getResultsByUserId(7L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo(7L);
    }

    @Test
    void shouldRejectAccessToAnotherUsersResultsForNonAdmin() {
        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));

        assertThatThrownBy(() -> resultService.getResultsByUserId(9L))
                .isInstanceOf(AccessDeniedBusinessException.class)
                .hasMessage("Solo puedes consultar tus propios resultados.");
    }

    @Test
    void shouldAllowAdminToReadAnyUsersResults() {
        Wod wod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Result result = new Result(1L, 9L, wod, "Fran", "Workout", "03:45", LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(1L, "admin@example.com", Set.of("ROLE_ADMIN")));
        when(resultRepository.findByUserIdOrderByCreatedAtDesc(9L)).thenReturn(List.of(result));

        List<ResultResponseDTO> responses = resultService.getResultsByUserId(9L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo(9L);
    }

    @Test
    void shouldUpdateOwnResult() {
        Wod originalWod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Wod updatedWod = new Wod(11L, "Murph", "Hero workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Result existing = new Result(5L, 7L, originalWod, "Fran", "Workout", "03:45", LocalDateTime.now());
        Result saved = new Result(5L, 7L, updatedWod, "Murph", "Hero workout", "39:10", existing.getCreatedAt());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(resultRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(resultRepository.existsByUserIdAndWodIdAndIdNot(7L, 11L, 5L)).thenReturn(false);
        when(wodRepository.findById(11L)).thenReturn(Optional.of(updatedWod));
        when(resultRepository.save(any(Result.class))).thenReturn(saved);

        ResultResponseDTO response = resultService.updateResult(5L, new ResultRequestDTO(11L, " 39:10 "));

        ArgumentCaptor<Result> resultCaptor = ArgumentCaptor.forClass(Result.class);
        verify(resultRepository).save(resultCaptor.capture());
        assertThat(resultCaptor.getValue().getWod().getId()).isEqualTo(11L);
        assertThat(resultCaptor.getValue().getWodName()).isEqualTo("Murph");
        assertThat(resultCaptor.getValue().getWodDescription()).isEqualTo("Hero workout");
        assertThat(resultCaptor.getValue().getResult()).isEqualTo("39:10");
        assertThat(response.getWodId()).isEqualTo(11L);
        assertThat(response.getWodName()).isEqualTo("Murph");
    }

    @Test
    void shouldRejectUpdatingAnotherUsersResultForNonAdmin() {
        Wod wod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Result existing = new Result(5L, 9L, wod, "Fran", "Workout", "03:45", LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(resultRepository.findById(5L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> resultService.updateResult(5L, new ResultRequestDTO(10L, "04:00")))
                .isInstanceOf(AccessDeniedBusinessException.class)
                .hasMessage("Solo puedes editar tus propios resultados.");
    }

    @Test
    void shouldAllowAdminToUpdateAnyResult() {
        Wod wod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Result existing = new Result(5L, 9L, wod, "Fran", "Workout", "03:45", LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(1L, "admin@example.com", Set.of("ROLE_ADMIN")));
        when(resultRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(resultRepository.existsByUserIdAndWodIdAndIdNot(9L, 10L, 5L)).thenReturn(false);
        when(wodRepository.findById(10L)).thenReturn(Optional.of(wod));
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResultResponseDTO response = resultService.updateResult(5L, new ResultRequestDTO(10L, "04:00"));

        assertThat(response.getUserId()).isEqualTo(9L);
        assertThat(response.getResult()).isEqualTo("04:00");
    }

    @Test
    void shouldRejectDuplicateResultWhenUpdating() {
        Wod wod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Result existing = new Result(5L, 7L, wod, "Fran", "Workout", "03:45", LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(resultRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(resultRepository.existsByUserIdAndWodIdAndIdNot(7L, 10L, 5L)).thenReturn(true);

        assertThatThrownBy(() -> resultService.updateResult(5L, new ResultRequestDTO(10L, "04:00")))
                .isInstanceOf(DuplicateResultException.class)
                .hasMessage("Ya existe un resultado guardado para este WOD. Edita el existente.");
    }

    @Test
    void shouldThrowWhenUpdatingUnknownResult() {
        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(resultRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultService.updateResult(5L, new ResultRequestDTO(10L, "04:00")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se ha encontrado el resultado solicitado");
    }

    @Test
    void shouldThrowWhenUpdatingResultForUnknownWod() {
        Wod wod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Result existing = new Result(5L, 7L, wod, "Fran", "Workout", "03:45", LocalDateTime.now());

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(resultRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(resultRepository.existsByUserIdAndWodIdAndIdNot(7L, 12L, 5L)).thenReturn(false);
        when(wodRepository.findById(12L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultService.updateResult(5L, new ResultRequestDTO(12L, "04:00")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se ha encontrado el WOD solicitado");
    }

    @Test
    void shouldReturnResultsByWodId() {
        Wod wod = new Wod(10L, "Fran", "Workout", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        Result result = new Result(1L, 7L, wod, "Fran", "Workout", "03:45", LocalDateTime.now());

        when(wodRepository.existsById(10L)).thenReturn(true);
        when(resultRepository.findByWodIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(result));

        List<ResultResponseDTO> responses = resultService.getResultsByWodId(10L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getWodId()).isEqualTo(10L);
        assertThat(responses.get(0).getWodName()).isEqualTo("Fran");
    }

    @Test
    void shouldThrowWhenGettingResultsForUnknownWod() {
        when(wodRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> resultService.getResultsByWodId(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se ha encontrado el WOD solicitado");
    }
}
