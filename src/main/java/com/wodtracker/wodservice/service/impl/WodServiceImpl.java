package com.wodtracker.wodservice.service.impl;

import com.wodtracker.wodservice.dto.WodRequestDTO;
import com.wodtracker.wodservice.dto.WodResponseDTO;
import com.wodtracker.wodservice.entity.Wod;
import com.wodtracker.wodservice.exception.DuplicateWodDateException;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.WodRepository;
import com.wodtracker.wodservice.service.WodService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class WodServiceImpl implements WodService {

    private final WodRepository wodRepository;

    public WodServiceImpl(WodRepository wodRepository) {
        this.wodRepository = wodRepository;
    }

    @Override
    public WodResponseDTO createWod(WodRequestDTO requestDTO) {
        validateUniqueDate(requestDTO.getDate(), null);
        Wod wod = new Wod();
        applyValues(wod, requestDTO);
        return toResponse(wodRepository.save(wod));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WodResponseDTO> getAllWods() {
        return wodRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WodResponseDTO getWodById(Long id) {
        return toResponse(findWodById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public WodResponseDTO getTodayWod() {
        return toResponse(wodRepository.findByDate(LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado un WOD para hoy.")));
    }

    @Override
    public WodResponseDTO updateWod(Long id, WodRequestDTO requestDTO) {
        Wod wod = findWodById(id);
        validateUniqueDate(requestDTO.getDate(), id);
        applyValues(wod, requestDTO);
        return toResponse(wodRepository.save(wod));
    }

    @Override
    public void deleteWod(Long id) {
        Wod wod = findWodById(id);
        wodRepository.delete(wod);
    }

    private Wod findWodById(Long id) {
        return wodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado el WOD solicitado"));
    }

    private void validateUniqueDate(LocalDate date, Long currentWodId) {
        if (date == null) {
            return;
        }

        boolean alreadyExists = currentWodId == null
                ? wodRepository.existsByDate(date)
                : wodRepository.existsByDateAndIdNot(date, currentWodId);

        if (alreadyExists) {
            throw new DuplicateWodDateException("Ya existe un WOD para la fecha " + date + ".");
        }
    }

    private void applyValues(Wod wod, WodRequestDTO requestDTO) {
        wod.setName(requestDTO.getName().trim());
        wod.setDescription(requestDTO.getDescription().trim());
        wod.setType(requestDTO.getType());
        wod.setDate(requestDTO.getDate());
        wod.setApproved(Boolean.TRUE.equals(requestDTO.getApproved()));
    }

    private WodResponseDTO toResponse(Wod wod) {
        return new WodResponseDTO(
                wod.getId(),
                wod.getName(),
                wod.getDescription(),
                wod.getType(),
                wod.getDate(),
                wod.isApproved()
        );
    }
}
