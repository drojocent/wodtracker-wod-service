package com.wodtracker.wodservice.service.impl;

import com.wodtracker.wodservice.dto.BenchmarkResultRequestDTO;
import com.wodtracker.wodservice.dto.BenchmarkResultResponseDTO;
import com.wodtracker.wodservice.entity.Benchmark;
import com.wodtracker.wodservice.entity.BenchmarkResult;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.BenchmarkRepository;
import com.wodtracker.wodservice.repository.BenchmarkResultRepository;
import com.wodtracker.wodservice.security.AuthenticatedUser;
import com.wodtracker.wodservice.security.AuthenticatedUserProvider;
import com.wodtracker.wodservice.service.BenchmarkResultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BenchmarkResultServiceImpl implements BenchmarkResultService {

    private final BenchmarkRepository benchmarkRepository;
    private final BenchmarkResultRepository benchmarkResultRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public BenchmarkResultServiceImpl(
            BenchmarkRepository benchmarkRepository,
            BenchmarkResultRepository benchmarkResultRepository,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.benchmarkRepository = benchmarkRepository;
        this.benchmarkResultRepository = benchmarkResultRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    public BenchmarkResultResponseDTO createResult(Long benchmarkId, BenchmarkResultRequestDTO requestDTO) {
        Benchmark benchmark = findBenchmarkById(benchmarkId);
        AuthenticatedUser authenticatedUser = authenticatedUserProvider.getAuthenticatedUser();

        BenchmarkResult benchmarkResult = new BenchmarkResult();
        benchmarkResult.setBenchmark(benchmark);
        benchmarkResult.setUserId(authenticatedUser.userId());
        benchmarkResult.setResult(requestDTO.getResult().trim());
        benchmarkResult.setCreatedAt(LocalDateTime.now());

        return toResponse(benchmarkResultRepository.save(benchmarkResult));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenchmarkResultResponseDTO> getMyResults(Long benchmarkId) {
        Benchmark benchmark = findBenchmarkById(benchmarkId);
        Long userId = authenticatedUserProvider.getAuthenticatedUser().userId();

        return benchmarkResultRepository.findByBenchmarkIdAndUserIdOrderByCreatedAtDesc(benchmark.getId(), userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Benchmark findBenchmarkById(Long benchmarkId) {
        return benchmarkRepository.findById(benchmarkId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se ha encontrado el benchmark solicitado"
                ));
    }

    private BenchmarkResultResponseDTO toResponse(BenchmarkResult benchmarkResult) {
        return new BenchmarkResultResponseDTO(
                benchmarkResult.getId(),
                benchmarkResult.getBenchmark().getId(),
                benchmarkResult.getUserId(),
                benchmarkResult.getResult(),
                benchmarkResult.getCreatedAt()
        );
    }
}
