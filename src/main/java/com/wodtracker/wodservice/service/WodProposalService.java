package com.wodtracker.wodservice.service;

import com.wodtracker.wodservice.dto.request.WodProposalRequestDTO;
import com.wodtracker.wodservice.dto.response.WodProposalResponseDTO;

import java.util.List;

public interface WodProposalService {

    WodProposalResponseDTO createProposal(WodProposalRequestDTO requestDTO);

    List<WodProposalResponseDTO> getPendingProposals();

    WodProposalResponseDTO approveProposal(Long proposalId);

    WodProposalResponseDTO rejectProposal(Long proposalId);
}
