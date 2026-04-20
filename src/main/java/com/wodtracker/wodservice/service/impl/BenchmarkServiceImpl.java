package com.wodtracker.wodservice.service.impl;

import com.wodtracker.wodservice.dto.BenchmarkRequestDTO;
import com.wodtracker.wodservice.dto.BenchmarkResponseDTO;
import com.wodtracker.wodservice.entity.Benchmark;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.BenchmarkRepository;
import com.wodtracker.wodservice.service.BenchmarkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BenchmarkServiceImpl implements BenchmarkService {

    private final BenchmarkRepository benchmarkRepository;

    public BenchmarkServiceImpl(BenchmarkRepository benchmarkRepository) {
        this.benchmarkRepository = benchmarkRepository;
    }

    @Override
    public BenchmarkResponseDTO createBenchmark(BenchmarkRequestDTO requestDTO) {
        Benchmark benchmark = new Benchmark();
        applyValues(benchmark, requestDTO);
        benchmark.setCreatedAt(LocalDateTime.now());
        return toResponse(benchmarkRepository.save(benchmark));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenchmarkResponseDTO> getAllBenchmarks() {
        return benchmarkRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BenchmarkResponseDTO getBenchmarkById(Long id) {
        return toResponse(findBenchmarkById(id));
    }

    @Override
    public BenchmarkResponseDTO updateBenchmark(Long id, BenchmarkRequestDTO requestDTO) {
        Benchmark benchmark = findBenchmarkById(id);
        applyValues(benchmark, requestDTO);
        return toResponse(benchmarkRepository.save(benchmark));
    }

    @Override
    public void deleteBenchmark(Long id) {
        Benchmark benchmark = findBenchmarkById(id);
        benchmarkRepository.delete(benchmark);
    }

    private Benchmark findBenchmarkById(Long id) {
        return benchmarkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Benchmark not found with id: " + id));
    }

    private void applyValues(Benchmark benchmark, BenchmarkRequestDTO requestDTO) {
        benchmark.setName(requestDTO.getName().trim());
        benchmark.setDescription(requestDTO.getDescription().trim());
        benchmark.setType(requestDTO.getType());
    }

    private BenchmarkResponseDTO toResponse(Benchmark benchmark) {
        return new BenchmarkResponseDTO(
                benchmark.getId(),
                benchmark.getName(),
                benchmark.getDescription(),
                benchmark.getType(),
                benchmark.getCreatedAt()
        );
    }
}
