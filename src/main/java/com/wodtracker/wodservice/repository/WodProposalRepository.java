package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.ProposalStatus;
import com.wodtracker.wodservice.entity.WodProposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WodProposalRepository extends JpaRepository<WodProposal, Long> {

    List<WodProposal> findByStatusOrderByCreatedAtAsc(ProposalStatus status);
}
