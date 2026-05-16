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
            ORDER BY CASE WHEN w.date IS NULL THEN 1 ELSE 0 END, w.date DESC
            """)
    List<Wod> findAllOrderByDateDescNullsLast();

    boolean existsByDate(LocalDate date);

    boolean existsByDateAndIdNot(LocalDate date, Long id);
}
