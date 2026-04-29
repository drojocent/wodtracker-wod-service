package com.wodtracker.wodservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenchmarkResultRequestDTO {

    @NotBlank(message = "El resultado es obligatorio.")
    @Size(max = 255, message = "El resultado no puede superar los 255 caracteres.")
    private String result;
}
