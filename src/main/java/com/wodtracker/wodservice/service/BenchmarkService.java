package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.BenchmarkRequestDTO;
import com.wodtracker.wodservice.dto.BenchmarkResponseDTO;

import java.util.List;

public interface BenchmarkService {

    BenchmarkResponseDTO createBenchmark(BenchmarkRequestDTO requestDTO);

    List<BenchmarkResponseDTO> getAllBenchmarks();

    BenchmarkResponseDTO getBenchmarkById(Long id);

    BenchmarkResponseDTO updateBenchmark(Long id, BenchmarkRequestDTO requestDTO);

    void deleteBenchmark(Long id);
}
