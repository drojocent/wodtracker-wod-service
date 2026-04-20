package com.wodtracker.wodservice.repository;

import com.wodtracker.wodservice.entity.Benchmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenchmarkRepository extends JpaRepository<Benchmark, Long> {
}
