package com.wodtracker.wodservice.service.impl;

import com.wodtracker.wodservice.dto.WodProposalRequestDTO;
import com.wodtracker.wodservice.dto.WodProposalResponseDTO;
import com.wodtracker.wodservice.entity.ProposalStatus;
import com.wodtracker.wodservice.entity.Wod;
import com.wodtracker.wodservice.entity.WodProposal;
import com.wodtracker.wodservice.exception.InvalidStateException;
import com.wodtracker.wodservice.exception.ResourceNotFoundException;
import com.wodtracker.wodservice.repository.WodProposalRepository;
import com.wodtracker.wodservice.repository.WodRepository;
import com.wodtracker.wodservice.security.AuthenticatedUserProvider;
import com.wodtracker.wodservice.service.WodProposalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WodProposalServiceImpl implements WodProposalService {

    private final WodProposalRepository wodProposalRepository;
    private final WodRepository wodRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public WodProposalServiceImpl(
            WodProposalRepository wodProposalRepository,
            WodRepository wodRepository,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.wodProposalRepository = wodProposalRepository;
        this.wodRepository = wodRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    public WodProposalResponseDTO createProposal(WodProposalRequestDTO requestDTO) {
        WodProposal proposal = new WodProposal();
        proposal.setUserId(authenticatedUserProvider.getAuthenticatedUser().userId());
        proposal.setName(requestDTO.getName().trim());
        proposal.setDescription(requestDTO.getDescription().trim());
        proposal.setType(requestDTO.getType());
        proposal.setStatus(ProposalStatus.PENDING);
        proposal.setCreatedAt(LocalDateTime.now());
        return toResponse(wodProposalRepository.save(proposal));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WodProposalResponseDTO> getPendingProposals() {
        return wodProposalRepository.findByStatusOrderByCreatedAtAsc(ProposalStatus.PENDING).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public WodProposalResponseDTO approveProposal(Long proposalId) {
        WodProposal proposal = findProposalById(proposalId);
        ensurePending(proposal);
        createApprovedWodFromProposal(proposal);
        proposal.setStatus(ProposalStatus.APPROVED);
        return toResponse(wodProposalRepository.save(proposal));
    }

    @Override
    public WodProposalResponseDTO rejectProposal(Long proposalId) {
        WodProposal proposal = findProposalById(proposalId);
        ensurePending(proposal);
        proposal.setStatus(ProposalStatus.REJECTED);
        return toResponse(wodProposalRepository.save(proposal));
    }

    private WodProposal findProposalById(Long proposalId) {
        return wodProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la propuesta solicitada"));
    }

    private void ensurePending(WodProposal proposal) {
        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new InvalidStateException("La propuesta ya esta en estado " + proposal.getStatus() + ".");
        }
    }

    private void createApprovedWodFromProposal(WodProposal proposal) {
        Wod wod = new Wod();
        wod.setName(proposal.getName());
        wod.setDescription(proposal.getDescription());
        wod.setType(proposal.getType());
        wod.setDate(null);
        wod.setApproved(true);
        wodRepository.save(wod);
    }

    private WodProposalResponseDTO toResponse(WodProposal proposal) {
        return new WodProposalResponseDTO(
                proposal.getId(),
                proposal.getUserId(),
                proposal.getName(),
                proposal.getDescription(),
                proposal.getType(),
                proposal.getStatus(),
                proposal.getCreatedAt()
        );
    }
}
