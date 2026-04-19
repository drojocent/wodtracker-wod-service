package com.wodtracker.wodservice.service.impl;

import com.wodtracker.wodservice.dto.ResultRequestDTO;
import com.wodtracker.wodservice.dto.ResultResponseDTO;
import com.wodtracker.wodservice.entity.Result;
import com.wodtracker.wodservice.entity.Wod;
import com.wodtracker.wodservice.exception.AccessDeniedBusinessException;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.ResultRepository;
import com.wodtracker.wodservice.repository.WodRepository;
import com.wodtracker.wodservice.security.AuthenticatedUser;
import com.wodtracker.wodservice.security.AuthenticatedUserProvider;
import com.wodtracker.wodservice.service.ResultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final WodRepository wodRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public ResultServiceImpl(
            ResultRepository resultRepository,
            WodRepository wodRepository,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.resultRepository = resultRepository;
        this.wodRepository = wodRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    public ResultResponseDTO createResult(ResultRequestDTO requestDTO) {
        AuthenticatedUser user = authenticatedUserProvider.getAuthenticatedUser();
        Wod wod = wodRepository.findById(requestDTO.getWodId())
                .orElseThrow(() -> new ResourceNotFoundException("WOD not found with id: " + requestDTO.getWodId()));

        Result result = new Result();
        result.setUserId(user.userId());
        result.setWod(wod);
        result.setResult(requestDTO.getResult().trim());
        result.setCreatedAt(LocalDateTime.now());
        return toResponse(resultRepository.save(result));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultResponseDTO> getResultsByUserId(Long userId) {
        AuthenticatedUser user = authenticatedUserProvider.getAuthenticatedUser();
        if (!user.isAdmin() && !user.userId().equals(userId)) {
            throw new AccessDeniedBusinessException("You can only access your own results");
        }

        return resultRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultResponseDTO> getResultsByWodId(Long wodId) {
        if (!wodRepository.existsById(wodId)) {
            throw new ResourceNotFoundException("WOD not found with id: " + wodId);
        }

        return resultRepository.findByWodIdOrderByCreatedAtDesc(wodId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ResultResponseDTO toResponse(Result result) {
        return new ResultResponseDTO(
                result.getId(),
                result.getUserId(),
                result.getWod().getId(),
                result.getWod().getName(),
                result.getResult(),
                result.getCreatedAt()
        );
    }
}
