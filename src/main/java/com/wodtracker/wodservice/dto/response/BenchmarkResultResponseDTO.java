package com.wodtracker.wodservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenchmarkResultResponseDTO {

    private Long id;
    private Long benchmarkId;
    private Long userId;
    private String result;
    private LocalDateTime createdAt;
}
