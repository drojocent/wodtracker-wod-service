package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Result> findByWodIdOrderByCreatedAtDesc(Long wodId);
}
