package com.wodtracker.wodservice.controller;

import com.wodtracker.wodservice.dto.ResultRequestDTO;
import com.wodtracker.wodservice.dto.ResultResponseDTO;
import com.wodtracker.wodservice.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @PostMapping
    @Operation(summary = "Register a WOD result", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResultResponseDTO> createResult(@Valid @RequestBody ResultRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resultService.createResult(requestDTO));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get results by user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ResultResponseDTO>> getResultsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(resultService.getResultsByUserId(userId));
    }

    @GetMapping("/wod/{wodId}")
    @Operation(summary = "Get results by WOD", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ResultResponseDTO>> getResultsByWodId(@PathVariable Long wodId) {
        return ResponseEntity.ok(resultService.getResultsByWodId(wodId));
    }
}
