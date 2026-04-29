package com.wodtracker.wodservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultRequestDTO {

    @NotNull(message = "El identificador del WOD es obligatorio.")
    private Long wodId;

    @NotBlank(message = "El resultado es obligatorio.")
    @Size(max = 255, message = "El resultado no puede superar los 255 caracteres.")
    private String result;
}
