package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.request.BenchmarkRequestDTO;
import com.wodtracker.wodservice.dto.response.BenchmarkResponseDTO;

import java.util.List;

public interface BenchmarkService {

    BenchmarkResponseDTO createBenchmark(BenchmarkRequestDTO requestDTO);

    List<BenchmarkResponseDTO> getAllBenchmarks();

    BenchmarkResponseDTO getBenchmarkById(Long id);

    BenchmarkResponseDTO updateBenchmark(Long id, BenchmarkRequestDTO requestDTO);

    void deleteBenchmark(Long id);
}
