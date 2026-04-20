package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.BenchmarkResultRequestDTO;
import com.wodtracker.wodservice.dto.BenchmarkResultResponseDTO;
import com.wodtracker.wodservice.entity.Benchmark;
import com.wodtracker.wodservice.entity.BenchmarkResult;
import com.wodtracker.wodservice.entity.BenchmarkType;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.BenchmarkRepository;
import com.wodtracker.wodservice.repository.BenchmarkResultRepository;
import com.wodtracker.wodservice.security.AuthenticatedUser;
import com.wodtracker.wodservice.security.AuthenticatedUserProvider;
import com.wodtracker.wodservice.service.impl.BenchmarkResultServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BenchmarkResultServiceTest {

    @Mock
    private BenchmarkRepository benchmarkRepository;

    @Mock
    private BenchmarkResultRepository benchmarkResultRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private BenchmarkResultServiceImpl benchmarkResultService;

    @Test
    void shouldCreateBenchmarkResult() {
        Benchmark benchmark = new Benchmark(10L, "Fran", "Workout", BenchmarkType.FOR_TIME, LocalDateTime.now(), List.of());
        BenchmarkResult saved = new BenchmarkResult(1L, benchmark, 7L, "03:45", LocalDateTime.now());

        when(benchmarkRepository.findById(10L)).thenReturn(Optional.of(benchmark));
        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(benchmarkResultRepository.save(any(BenchmarkResult.class))).thenReturn(saved);

        BenchmarkResultResponseDTO response = benchmarkResultService.createResult(10L, new BenchmarkResultRequestDTO(" 03:45 "));

        ArgumentCaptor<BenchmarkResult> captor = ArgumentCaptor.forClass(BenchmarkResult.class);
        verify(benchmarkResultRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(7L);
        assertThat(captor.getValue().getResult()).isEqualTo("03:45");
        assertThat(response.getBenchmarkId()).isEqualTo(10L);
    }

    @Test
    void shouldReturnMyResultsOrderedByDateDesc() {
        Benchmark benchmark = new Benchmark(10L, "Fran", "Workout", BenchmarkType.FOR_TIME, LocalDateTime.now(), List.of());
        BenchmarkResult result = new BenchmarkResult(1L, benchmark, 7L, "03:45", LocalDateTime.now());

        when(benchmarkRepository.findById(10L)).thenReturn(Optional.of(benchmark));
        when(authenticatedUserProvider.getAuthenticatedUser())
                .thenReturn(new AuthenticatedUser(7L, "athlete@example.com", Set.of("ROLE_USER")));
        when(benchmarkResultRepository.findByBenchmarkIdAndUserIdOrderByCreatedAtDesc(10L, 7L)).thenReturn(List.of(result));

        List<BenchmarkResultResponseDTO> responses = benchmarkResultService.getMyResults(10L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo(7L);
        assertThat(responses.get(0).getResult()).isEqualTo("03:45");
    }

    @Test
    void shouldThrowWhenBenchmarkDoesNotExistForCreate() {
        when(benchmarkRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> benchmarkResultService.createResult(99L, new BenchmarkResultRequestDTO("03:45")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Benchmark not found with id: 99");
    }
}
