package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.Result;
import com.wodtracker.wodservice.entity.Wod;
import com.wodtracker.wodservice.entity.WodType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ResultRepositoryTest {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private WodRepository wodRepository;

    @Test
    void shouldFindResultsByUserIdOrderedByCreatedAtDesc() {
        Wod wod = new Wod();
        wod.setName("Fran");
        wod.setDescription("Workout");
        wod.setType(WodType.FOR_TIME);
        wod.setDate(LocalDate.now());
        wod.setApproved(true);
        Wod savedWod = wodRepository.save(wod);

        Result older = new Result(null, 7L, savedWod, "Fran", "Workout", "04:00", LocalDateTime.now().minusMinutes(5));
        Result newer = new Result(null, 7L, savedWod, "Fran", "Workout", "03:45", LocalDateTime.now());
        resultRepository.save(older);
        resultRepository.save(newer);

        assertThat(resultRepository.findByUserIdOrderByCreatedAtDesc(7L))
                .extracting(Result::getResult)
                .containsExactly("03:45", "04:00");
    }
}
