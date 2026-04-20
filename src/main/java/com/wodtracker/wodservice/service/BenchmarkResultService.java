package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.BenchmarkResultRequestDTO;
import com.wodtracker.wodservice.dto.BenchmarkResultResponseDTO;

import java.util.List;

public interface BenchmarkResultService {

    BenchmarkResultResponseDTO createResult(Long benchmarkId, BenchmarkResultRequestDTO requestDTO);

    List<BenchmarkResultResponseDTO> getMyResults(Long benchmarkId);
}
