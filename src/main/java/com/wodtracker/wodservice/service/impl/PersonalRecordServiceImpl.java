package com.wodtracker.wodservice.service.impl;

import com.wodtracker.wodservice.dto.request.PersonalRecordRequestDTO;
import com.wodtracker.wodservice.dto.response.PersonalRecordResponseDTO;
import com.wodtracker.wodservice.entity.ExerciseType;
import com.wodtracker.wodservice.entity.PersonalRecord;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.PersonalRecordRepository;
import com.wodtracker.wodservice.security.AuthenticatedUserProvider;
import com.wodtracker.wodservice.service.PersonalRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class PersonalRecordServiceImpl implements PersonalRecordService {

    private final PersonalRecordRepository personalRecordRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public PersonalRecordServiceImpl(
            PersonalRecordRepository personalRecordRepository,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.personalRecordRepository = personalRecordRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseType> getExercises() {
        return Arrays.asList(ExerciseType.values());
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalRecordResponseDTO getMyPersonalRecord(ExerciseType exercise) {
        Long userId = authenticatedUserProvider.getAuthenticatedUser().userId();

        return personalRecordRepository.findFirstByUserIdAndExerciseOrderByWeightDescCreatedAtDesc(userId, exercise)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se ha encontrado un record personal para el ejercicio "+ exercise + "."
                ));
    }

    @Override
    public PersonalRecordResponseDTO createPersonalRecord(ExerciseType exercise, PersonalRecordRequestDTO requestDTO) {
        PersonalRecord personalRecord = new PersonalRecord();
        personalRecord.setUserId(authenticatedUserProvider.getAuthenticatedUser().userId());
        personalRecord.setExercise(exercise);
        personalRecord.setWeight(requestDTO.getWeight());
        personalRecord.setCreatedAt(LocalDateTime.now());

        return toResponse(personalRecordRepository.save(personalRecord));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalRecordResponseDTO> getMyHistory(ExerciseType exercise) {
        Long userId = authenticatedUserProvider.getAuthenticatedUser().userId();

        return personalRecordRepository.findByUserIdAndExerciseOrderByCreatedAtDesc(userId, exercise).stream()
                .map(this::toResponse)
                .toList();
    }

    private PersonalRecordResponseDTO toResponse(PersonalRecord personalRecord) {
        return new PersonalRecordResponseDTO(
                personalRecord.getId(),
                personalRecord.getUserId(),
                personalRecord.getExercise(),
                personalRecord.getWeight(),
                personalRecord.getCreatedAt()
        );
    }
}
