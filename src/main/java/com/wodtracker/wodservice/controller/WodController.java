package com.wodtracker.wodservice.controller;

import com.wodtracker.wodservice.dto.request.WodRequestDTO;
import com.wodtracker.wodservice.dto.response.WodResponseDTO;
import com.wodtracker.wodservice.service.WodService;
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
@RequestMapping("/wods")
public class WodController {

    private final WodService wodService;

    public WodController(WodService wodService) {
        this.wodService = wodService;
    }

    @PostMapping
    @Operation(summary = "Create a WOD", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<WodResponseDTO> createWod(@Valid @RequestBody WodRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wodService.createWod(requestDTO));
    }

    @GetMapping
    @Operation(summary = "List WODs", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<WodResponseDTO>> getAllWods() {
        return ResponseEntity.ok(wodService.getAllWods());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get WOD by id", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<WodResponseDTO> getWodById(@PathVariable Long id) {
        return ResponseEntity.ok(wodService.getWodById(id));
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's WOD", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<WodResponseDTO> getTodayWod() {
        return ResponseEntity.ok(wodService.getTodayWod());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update WOD", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<WodResponseDTO> updateWod(@PathVariable Long id, @Valid @RequestBody WodRequestDTO requestDTO) {
        return ResponseEntity.ok(wodService.updateWod(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete WOD", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteWod(@PathVariable Long id) {
        wodService.deleteWod(id);
        return ResponseEntity.noContent().build();
    }
}
