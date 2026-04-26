package com.wodtracker.wodservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRecordRequestDTO {

    @NotNull(message = "El peso es obligatorio.")
    @DecimalMin(value = "0.01", message = "El peso debe ser mayor que 0.")
    private BigDecimal weight;
}
