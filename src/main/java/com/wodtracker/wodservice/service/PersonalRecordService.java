package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.PersonalRecordRequestDTO;
import com.wodtracker.wodservice.dto.PersonalRecordResponseDTO;
import com.wodtracker.wodservice.entity.ExerciseType;

import java.util.List;

public interface PersonalRecordService {

    List<ExerciseType> getExercises();

    PersonalRecordResponseDTO getMyPersonalRecord(ExerciseType exercise);

    PersonalRecordResponseDTO createPersonalRecord(ExerciseType exercise, PersonalRecordRequestDTO requestDTO);

    List<PersonalRecordResponseDTO> getMyHistory(ExerciseType exercise);
}
