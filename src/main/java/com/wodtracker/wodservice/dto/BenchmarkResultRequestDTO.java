package com.wodtracker.wodservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenchmarkResultRequestDTO {

    @NotBlank(message = "Result is required")
    @Size(max = 255, message = "Result must not exceed 255 characters")
    private String result;
}
