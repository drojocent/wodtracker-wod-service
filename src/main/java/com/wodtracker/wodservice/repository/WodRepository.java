package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.Wod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WodRepository extends JpaRepository<Wod, Long> {

    Optional<Wod> findByDate(LocalDate date);

    @Query("""
            SELECT w
            FROM Wod w
            WHERE w.date IS NULL OR w.date >= :today
            ORDER BY
                CASE
                    WHEN w.date = :today THEN 0
                    WHEN w.date IS NULL THEN 2
                    ELSE 1
                END,
                w.date ASC
            """)
    List<Wod> findVisibleWodsOrderedFromToday(LocalDate today);

    boolean existsByDate(LocalDate date);

    boolean existsByDateAndIdNot(LocalDate date, Long id);
}
