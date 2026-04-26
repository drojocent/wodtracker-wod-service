package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.BenchmarkRequestDTO;
import com.wodtracker.wodservice.dto.BenchmarkResponseDTO;
import com.wodtracker.wodservice.entity.Benchmark;
import com.wodtracker.wodservice.entity.BenchmarkType;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.BenchmarkRepository;
import com.wodtracker.wodservice.service.impl.BenchmarkServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BenchmarkServiceTest {

    @Mock
    private BenchmarkRepository benchmarkRepository;

    @InjectMocks
    private BenchmarkServiceImpl benchmarkService;

    @Test
    void shouldCreateBenchmark() {
        Benchmark saved = new Benchmark(1L, "Fran", "Workout", BenchmarkType.FOR_TIME, LocalDateTime.now(), List.of());

        when(benchmarkRepository.save(any(Benchmark.class))).thenReturn(saved);

        BenchmarkResponseDTO response = benchmarkService.createBenchmark(
                new BenchmarkRequestDTO(" Fran ", " Workout ", BenchmarkType.FOR_TIME)
        );

        assertThat(response.getName()).isEqualTo("Fran");
        assertThat(response.getDescription()).isEqualTo("Workout");
        assertThat(response.getType()).isEqualTo(BenchmarkType.FOR_TIME);
    }

    @Test
    void shouldReturnBenchmarkById() {
        Benchmark benchmark = new Benchmark(1L, "Cindy", "Workout", BenchmarkType.AMRAP, LocalDateTime.now(), List.of());
        when(benchmarkRepository.findById(1L)).thenReturn(Optional.of(benchmark));

        BenchmarkResponseDTO response = benchmarkService.getBenchmarkById(1L);

        assertThat(response.getName()).isEqualTo("Cindy");
        assertThat(response.getType()).isEqualTo(BenchmarkType.AMRAP);
    }

    @Test
    void shouldUpdateBenchmark() {
        Benchmark benchmark = new Benchmark(1L, "Old", "Old workout", BenchmarkType.EMOM, LocalDateTime.now(), List.of());
        Benchmark saved = new Benchmark(1L, "New", "Updated workout", BenchmarkType.FOR_TIME, benchmark.getCreatedAt(), List.of());

        when(benchmarkRepository.findById(1L)).thenReturn(Optional.of(benchmark));
        when(benchmarkRepository.save(any(Benchmark.class))).thenReturn(saved);

        BenchmarkResponseDTO response = benchmarkService.updateBenchmark(
                1L,
                new BenchmarkRequestDTO(" New ", " Updated workout ", BenchmarkType.FOR_TIME)
        );

        verify(benchmarkRepository).save(benchmark);
        assertThat(response.getName()).isEqualTo("New");
        assertThat(response.getDescription()).isEqualTo("Updated workout");
    }

    @Test
    void shouldDeleteBenchmark() {
        Benchmark benchmark = new Benchmark(1L, "Fran", "Workout", BenchmarkType.FOR_TIME, LocalDateTime.now(), List.of());
        when(benchmarkRepository.findById(1L)).thenReturn(Optional.of(benchmark));

        benchmarkService.deleteBenchmark(1L);

        verify(benchmarkRepository).delete(benchmark);
    }

    @Test
    void shouldThrowWhenBenchmarkDoesNotExist() {
        when(benchmarkRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> benchmarkService.getBenchmarkById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se ha encontrado el benchmark solicitado");
    }
}
