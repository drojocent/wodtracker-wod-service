package com.wodtracker.wodservice.dto;

import com.wodtracker.wodservice.entity.BenchmarkType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenchmarkResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BenchmarkType type;
    private LocalDateTime createdAt;
}
