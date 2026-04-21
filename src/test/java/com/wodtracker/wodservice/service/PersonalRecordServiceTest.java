package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.PersonalRecordRequestDTO;
import com.wodtracker.wodservice.dto.PersonalRecordResponseDTO;
import com.wodtracker.wodservice.entity.ExerciseType;
import com.wodtracker.wodservice.entity.PersonalRecord;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.PersonalRecordRepository;
import com.wodtracker.wodservice.security.AuthenticatedUser;
import com.wodtracker.wodservice.security.AuthenticatedUserProvider;
import com.wodtracker.wodservice.service.impl.PersonalRecordServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class PersonalRecordServiceTest {

    @Mock
    private PersonalRecordRepository personalRecordRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private PersonalRecordServiceImpl personalRecordService;

    @Test
    void shouldListAllExercises() {
        List<ExerciseType> exercises = personalRecordService.getExercises();

        assertThat(exercises).contains(ExerciseType.BACK_SQUAT, ExerciseType.CLEAN_AND_JERK, ExerciseType.OVERHEAD_SQUAT);
        assertThat(exercises).hasSize(12);
    }

    @Test
    void shouldReturnCurrentPersonalRecord() {
        PersonalRecord personalRecord = new PersonalRecord(
                1L,
                7L,
                ExerciseType.BACK_SQUAT,
                new BigDecimal("145.00"),
                LocalDateTime.now()
        );

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(personalRecordRepository.findFirstByUserIdAndExerciseOrderByWeightDescCreatedAtDesc(7L, ExerciseType.BACK_SQUAT))
                .thenReturn(Optional.of(personalRecord));

        PersonalRecordResponseDTO response = personalRecordService.getMyPersonalRecord(ExerciseType.BACK_SQUAT);

        assertThat(response.getUserId()).isEqualTo(7L);
        assertThat(response.getExercise()).isEqualTo(ExerciseType.BACK_SQUAT);
        assertThat(response.getWeight()).isEqualByComparingTo("145.00");
    }

    @Test
    void shouldThrowWhenCurrentPersonalRecordDoesNotExist() {
        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(personalRecordRepository.findFirstByUserIdAndExerciseOrderByWeightDescCreatedAtDesc(7L, ExerciseType.SNATCH))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> personalRecordService.getMyPersonalRecord(ExerciseType.SNATCH))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Personal record not found for exercise: SNATCH");
    }

    @Test
    void shouldCreatePersonalRecordForAuthenticatedUser() {
        PersonalRecord saved = new PersonalRecord(
                1L,
                7L,
                ExerciseType.DEADLIFT,
                new BigDecimal("180.00"),
                LocalDateTime.now()
        );

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(personalRecordRepository.save(any(PersonalRecord.class))).thenReturn(saved);

        PersonalRecordResponseDTO response = personalRecordService.createPersonalRecord(
                ExerciseType.DEADLIFT,
                new PersonalRecordRequestDTO(new BigDecimal("180.00"))
        );

        ArgumentCaptor<PersonalRecord> captor = ArgumentCaptor.forClass(PersonalRecord.class);
        verify(personalRecordRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(7L);
        assertThat(captor.getValue().getExercise()).isEqualTo(ExerciseType.DEADLIFT);
        assertThat(captor.getValue().getWeight()).isEqualByComparingTo("180.00");
        assertThat(response.getExercise()).isEqualTo(ExerciseType.DEADLIFT);
    }

    @Test
    void shouldReturnHistoryOrderedByDateDesc() {
        PersonalRecord first = new PersonalRecord(
                2L,
                7L,
                ExerciseType.CLEAN,
                new BigDecimal("100.00"),
                LocalDateTime.now()
        );
        PersonalRecord second = new PersonalRecord(
                1L,
                7L,
                ExerciseType.CLEAN,
                new BigDecimal("95.00"),
                LocalDateTime.now().minusDays(5)
        );

        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(personalRecordRepository.findByUserIdAndExerciseOrderByCreatedAtDesc(7L, ExerciseType.CLEAN))
                .thenReturn(List.of(first, second));

        List<PersonalRecordResponseDTO> responses = personalRecordService.getMyHistory(ExerciseType.CLEAN);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getWeight()).isEqualByComparingTo("100.00");
        assertThat(responses.get(1).getWeight()).isEqualByComparingTo("95.00");
    }
}
