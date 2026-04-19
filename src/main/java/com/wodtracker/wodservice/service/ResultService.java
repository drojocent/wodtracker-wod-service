package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.ResultRequestDTO;
import com.wodtracker.wodservice.dto.ResultResponseDTO;

import java.util.List;

public interface ResultService {

    ResultResponseDTO createResult(ResultRequestDTO requestDTO);

    List<ResultResponseDTO> getResultsByUserId(Long userId);

    List<ResultResponseDTO> getResultsByWodId(Long wodId);
}
