package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.WodRequestDTO;
import com.wodtracker.wodservice.dto.WodResponseDTO;
import com.wodtracker.wodservice.entity.Wod;
import com.wodtracker.wodservice.entity.WodType;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.WodRepository;
import com.wodtracker.wodservice.service.impl.WodServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WodServiceTest {

    @Mock
    private WodRepository wodRepository;

    @InjectMocks
    private WodServiceImpl wodService;

    @Test
    void shouldCreateWodSuccessfully() {
        WodRequestDTO requestDTO = new WodRequestDTO(" Fran ", " Desc ", WodType.FOR_TIME, LocalDate.now(), true);
        Wod savedWod = new Wod(1L, "Fran", "Desc", WodType.FOR_TIME, LocalDate.now(), true, List.of());

        when(wodRepository.save(any(Wod.class))).thenReturn(savedWod);

        WodResponseDTO response = wodService.createWod(requestDTO);

        ArgumentCaptor<Wod> wodCaptor = ArgumentCaptor.forClass(Wod.class);
        verify(wodRepository).save(wodCaptor.capture());
        assertThat(wodCaptor.getValue().getName()).isEqualTo("Fran");
        assertThat(wodCaptor.getValue().getDescription()).isEqualTo("Desc");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getType()).isEqualTo(WodType.FOR_TIME);
        assertThat(response.isApproved()).isTrue();
    }

    @Test
    void shouldReturnTodayWod() {
        Wod wod = new Wod(1L, "Murph", "For time", WodType.FOR_TIME, LocalDate.now(), true, List.of());
        when(wodRepository.findByDate(LocalDate.now())).thenReturn(Optional.of(wod));

        WodResponseDTO response = wodService.getTodayWod();

        assertThat(response.getName()).isEqualTo("Murph");
        assertThat(response.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void shouldThrowWhenTodayWodDoesNotExist() {
        when(wodRepository.findByDate(LocalDate.now())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wodService.getTodayWod())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se ha encontrado un WOD para hoy.");
    }

    @Test
    void shouldReturnOnlyCurrentAndFutureWods() {
        LocalDate today = LocalDate.now();
        Wod todayWod = new Wod(1L, "Hoy", "Workout de hoy", WodType.FOR_TIME, today, true, List.of());
        Wod futureWod = new Wod(2L, "Manana", "Workout futuro", WodType.AMRAP, today.plusDays(1), true, List.of());

        when(wodRepository.findByDateGreaterThanEqualOrderByDateAsc(eq(today)))
                .thenReturn(List.of(todayWod, futureWod));

        List<WodResponseDTO> response = wodService.getAllWods();

        assertThat(response).hasSize(2);
        assertThat(response).extracting(WodResponseDTO::getName)
                .containsExactly("Hoy", "Manana");
        verify(wodRepository).findByDateGreaterThanEqualOrderByDateAsc(today);
    }

    @Test
    void shouldUpdateExistingWod() {
        Wod existing = new Wod(5L, "Old", "Old desc", WodType.EMOM, LocalDate.now(), false, List.of());
        Wod updated = new Wod(5L, "New", "New desc", WodType.AMRAP, LocalDate.now().plusDays(1), true, List.of());
        WodRequestDTO requestDTO = new WodRequestDTO("New", "New desc", WodType.AMRAP, LocalDate.now().plusDays(1), true);

        when(wodRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(wodRepository.save(existing)).thenReturn(updated);

        WodResponseDTO response = wodService.updateWod(5L, requestDTO);

        assertThat(existing.getName()).isEqualTo("New");
        assertThat(existing.getType()).isEqualTo(WodType.AMRAP);
        assertThat(response.getName()).isEqualTo("New");
        assertThat(response.isApproved()).isTrue();
    }

    @Test
    void shouldDeleteExistingWod() {
        Wod existing = new Wod(5L, "Old", "Old desc", WodType.EMOM, LocalDate.now(), false, List.of());
        when(wodRepository.findById(5L)).thenReturn(Optional.of(existing));

        wodService.deleteWod(5L);

        verify(wodRepository).delete(existing);
    }
}
