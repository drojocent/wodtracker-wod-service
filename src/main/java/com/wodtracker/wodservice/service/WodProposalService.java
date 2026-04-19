package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.WodProposalRequestDTO;
import com.wodtracker.wodservice.dto.WodProposalResponseDTO;

import java.util.List;

public interface WodProposalService {

    WodProposalResponseDTO createProposal(WodProposalRequestDTO requestDTO);

    List<WodProposalResponseDTO> getPendingProposals();

    WodProposalResponseDTO approveProposal(Long proposalId);

    WodProposalResponseDTO rejectProposal(Long proposalId);
}
