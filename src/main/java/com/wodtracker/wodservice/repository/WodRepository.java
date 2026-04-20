package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.Wod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WodRepository extends JpaRepository<Wod, Long> {

    Optional<Wod> findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);

    boolean existsByDateAndIdNot(LocalDate date, Long id);
}
