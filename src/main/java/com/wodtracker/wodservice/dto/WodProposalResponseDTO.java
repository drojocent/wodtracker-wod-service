package com.wodtracker.wodservice.dto;

import com.wodtracker.wodservice.entity.ProposalStatus;
import com.wodtracker.wodservice.entity.WodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WodProposalResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String description;
    private WodType type;
    private ProposalStatus status;
    private LocalDateTime createdAt;
}
