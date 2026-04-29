package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.request.WodRequestDTO;
import com.wodtracker.wodservice.dto.response.WodResponseDTO;

import java.util.List;

public interface WodService {

    WodResponseDTO createWod(WodRequestDTO requestDTO);

    List<WodResponseDTO> getAllWods();

    WodResponseDTO getWodById(Long id);

    WodResponseDTO getTodayWod();

    WodResponseDTO updateWod(Long id, WodRequestDTO requestDTO);

    void deleteWod(Long id);
}
