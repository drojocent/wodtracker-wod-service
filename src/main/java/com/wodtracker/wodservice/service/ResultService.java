package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.request.ResultRequestDTO;
import com.wodtracker.wodservice.dto.response.ResultResponseDTO;

import java.util.List;

public interface ResultService {

    ResultResponseDTO createResult(ResultRequestDTO requestDTO);

    ResultResponseDTO updateResult(Long resultId, ResultRequestDTO requestDTO);

    List<ResultResponseDTO> getResultsByUserId(Long userId);

    List<ResultResponseDTO> getResultsByWodId(Long wodId);
}
