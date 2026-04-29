package com.wodtracker.wodservice.controller;

import com.wodtracker.wodservice.dto.request.PersonalRecordRequestDTO;
import com.wodtracker.wodservice.dto.response.PersonalRecordResponseDTO;
import com.wodtracker.wodservice.entity.ExerciseType;
import com.wodtracker.wodservice.service.PersonalRecordService;
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
@RequestMapping("/prs")
public class PersonalRecordController {

    private final PersonalRecordService personalRecordService;

    public PersonalRecordController(PersonalRecordService personalRecordService) {
        this.personalRecordService = personalRecordService;
    }

    @GetMapping("/exercises")
    @Operation(summary = "List predefined PR exercises", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ExerciseType>> getExercises() {
        return ResponseEntity.ok(personalRecordService.getExercises());
    }

    @GetMapping("/{exercise}/me")
    @Operation(summary = "Get my current PR for an exercise", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PersonalRecordResponseDTO> getMyPersonalRecord(@PathVariable ExerciseType exercise) {
        return ResponseEntity.ok(personalRecordService.getMyPersonalRecord(exercise));
    }

    @PostMapping("/{exercise}")
    @Operation(summary = "Create a PR entry for an exercise", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PersonalRecordResponseDTO> createPersonalRecord(
            @PathVariable ExerciseType exercise,
            @Valid @RequestBody PersonalRecordRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personalRecordService.createPersonalRecord(exercise, requestDTO));
    }

    @GetMapping("/{exercise}/me/history")
    @Operation(summary = "Get my PR history for an exercise", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<PersonalRecordResponseDTO>> getMyHistory(@PathVariable ExerciseType exercise) {
        return ResponseEntity.ok(personalRecordService.getMyHistory(exercise));
    }
}
