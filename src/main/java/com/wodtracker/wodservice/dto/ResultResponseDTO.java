package com.wodtracker.wodservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponseDTO {

    private Long id;
    private Long userId;
    private Long wodId;
    private String wodName;
    private String result;
    private LocalDateTime createdAt;
}
