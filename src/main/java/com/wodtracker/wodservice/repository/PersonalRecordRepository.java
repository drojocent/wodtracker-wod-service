package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.ExerciseType;
import com.wodtracker.wodservice.entity.PersonalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonalRecordRepository extends JpaRepository<PersonalRecord, Long> {

    Optional<PersonalRecord> findFirstByUserIdAndExerciseOrderByWeightDescCreatedAtDesc(Long userId, ExerciseType exercise);

    List<PersonalRecord> findByUserIdAndExerciseOrderByCreatedAtDesc(Long userId, ExerciseType exercise);
}
