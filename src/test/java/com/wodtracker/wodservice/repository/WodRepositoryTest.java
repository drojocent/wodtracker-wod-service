package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.Wod;
import com.wodtracker.wodservice.entity.WodType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class WodRepositoryTest {

    @Autowired
    private WodRepository wodRepository;

    @Test
    void shouldFindWodByDate() {
        Wod wod = new Wod();
        wod.setName("Murph");
        wod.setDescription("For time");
        wod.setType(WodType.FOR_TIME);
        wod.setDate(LocalDate.now());
        wod.setApproved(true);
        wodRepository.save(wod);

        assertThat(wodRepository.findByDate(LocalDate.now())).isPresent();
    }
}
