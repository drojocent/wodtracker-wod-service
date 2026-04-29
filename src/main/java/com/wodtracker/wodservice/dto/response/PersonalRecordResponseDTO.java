package com.wodtracker.wodservice.dto.response;

import com.wodtracker.wodservice.entity.ExerciseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRecordResponseDTO {

    private Long id;
    private Long userId;
    private ExerciseType exercise;
    private BigDecimal weight;
    private LocalDateTime createdAt;
}
