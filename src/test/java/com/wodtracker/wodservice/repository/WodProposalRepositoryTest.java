package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.ProposalStatus;
import com.wodtracker.wodservice.entity.WodProposal;
import com.wodtracker.wodservice.entity.WodType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class WodProposalRepositoryTest {

    @Autowired
    private WodProposalRepository wodProposalRepository;

    @Test
    void shouldFindPendingProposalsOrderedByCreatedAtAsc() {
        WodProposal later = new WodProposal(null, 7L, "Later", "Workout", WodType.AMRAP, ProposalStatus.PENDING, LocalDateTime.now());
        WodProposal earlier = new WodProposal(null, 8L, "Earlier", "Workout", WodType.EMOM, ProposalStatus.PENDING, LocalDateTime.now().minusMinutes(10));
        wodProposalRepository.save(later);
        wodProposalRepository.save(earlier);

        assertThat(wodProposalRepository.findByStatusOrderByCreatedAtAsc(ProposalStatus.PENDING))
                .extracting(WodProposal::getName)
                .containsExactly("Earlier", "Later");
    }
}
