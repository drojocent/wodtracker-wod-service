package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.request.BenchmarkResultRequestDTO;
import com.wodtracker.wodservice.dto.response.BenchmarkResultResponseDTO;

import java.util.List;

public interface BenchmarkResultService {

    BenchmarkResultResponseDTO createResult(Long benchmarkId, BenchmarkResultRequestDTO requestDTO);

    List<BenchmarkResultResponseDTO> getMyResults(Long benchmarkId);
}
