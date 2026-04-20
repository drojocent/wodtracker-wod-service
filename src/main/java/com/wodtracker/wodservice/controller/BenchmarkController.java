package com.wodtracker.wodservice.controller;

import com.wodtracker.wodservice.dto.BenchmarkRequestDTO;
import com.wodtracker.wodservice.dto.BenchmarkResponseDTO;
import com.wodtracker.wodservice.dto.BenchmarkResultRequestDTO;
import com.wodtracker.wodservice.dto.BenchmarkResultResponseDTO;
import com.wodtracker.wodservice.service.BenchmarkResultService;
import com.wodtracker.wodservice.service.BenchmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/benchmarks")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;
    private final BenchmarkResultService benchmarkResultService;

    public BenchmarkController(BenchmarkService benchmarkService, BenchmarkResultService benchmarkResultService) {
        this.benchmarkService = benchmarkService;
        this.benchmarkResultService = benchmarkResultService;
    }

    @GetMapping
    @Operation(summary = "List benchmarks", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<BenchmarkResponseDTO>> getAllBenchmarks() {
        return ResponseEntity.ok(benchmarkService.getAllBenchmarks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get benchmark by id", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BenchmarkResponseDTO> getBenchmarkById(@PathVariable Long id) {
        return ResponseEntity.ok(benchmarkService.getBenchmarkById(id));
    }

    @PostMapping
    @Operation(summary = "Create a benchmark", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BenchmarkResponseDTO> createBenchmark(@Valid @RequestBody BenchmarkRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(benchmarkService.createBenchmark(requestDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a benchmark", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BenchmarkResponseDTO> updateBenchmark(@PathVariable Long id, @Valid @RequestBody BenchmarkRequestDTO requestDTO) {
        return ResponseEntity.ok(benchmarkService.updateBenchmark(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a benchmark", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteBenchmark(@PathVariable Long id) {
        benchmarkService.deleteBenchmark(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/results")
    @Operation(summary = "Register a benchmark result", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BenchmarkResultResponseDTO> createResult(
            @PathVariable Long id,
            @Valid @RequestBody BenchmarkResultRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(benchmarkResultService.createResult(id, requestDTO));
    }

    @GetMapping("/{id}/results/me")
    @Operation(summary = "Get my benchmark results", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<BenchmarkResultResponseDTO>> getMyResults(@PathVariable Long id) {
        return ResponseEntity.ok(benchmarkResultService.getMyResults(id));
    }
}
