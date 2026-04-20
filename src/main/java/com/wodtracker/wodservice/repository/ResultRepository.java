package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Result> findByWodIdOrderByCreatedAtDesc(Long wodId);

    Optional<Result> findByUserIdAndWodId(Long userId, Long wodId);

    boolean existsByUserIdAndWodId(Long userId, Long wodId);

    boolean existsByUserIdAndWodIdAndIdNot(Long userId, Long wodId, Long id);
}
