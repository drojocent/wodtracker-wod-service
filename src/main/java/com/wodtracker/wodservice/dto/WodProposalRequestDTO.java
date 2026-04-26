package com.wodtracker.wodservice.dto;

import com.wodtracker.wodservice.entity.WodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WodProposalRequestDTO {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres.")
    private String name;

    @NotBlank(message = "La descripcion es obligatoria.")
    private String description;

    @NotNull(message = "El tipo es obligatorio.")
    private WodType type;
}
