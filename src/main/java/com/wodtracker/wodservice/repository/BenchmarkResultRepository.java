package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.BenchmarkResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenchmarkResultRepository extends JpaRepository<BenchmarkResult, Long> {

    List<BenchmarkResult> findByBenchmarkIdAndUserIdOrderByCreatedAtDesc(Long benchmarkId, Long userId);
}
