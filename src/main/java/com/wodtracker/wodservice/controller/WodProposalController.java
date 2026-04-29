package com.wodtracker.wodservice.controller;

import com.wodtracker.wodservice.dto.request.WodProposalRequestDTO;
import com.wodtracker.wodservice.dto.response.WodProposalResponseDTO;
import com.wodtracker.wodservice.service.WodProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/proposals")
public class WodProposalController {

    private final WodProposalService wodProposalService;

    public WodProposalController(WodProposalService wodProposalService) {
        this.wodProposalService = wodProposalService;
    }

    @PostMapping
    @Operation(summary = "Create a WOD proposal", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<WodProposalResponseDTO> createProposal(@Valid @RequestBody WodProposalRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wodProposalService.createProposal(requestDTO));
    }

    @GetMapping("/pending")
    @Operation(summary = "List pending proposals", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<WodProposalResponseDTO>> getPendingProposals() {
        return ResponseEntity.ok(wodProposalService.getPendingProposals());
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve a proposal", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<WodProposalResponseDTO> approveProposal(@PathVariable Long id) {
        return ResponseEntity.ok(wodProposalService.approveProposal(id));
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject a proposal", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<WodProposalResponseDTO> rejectProposal(@PathVariable Long id) {
        return ResponseEntity.ok(wodProposalService.rejectProposal(id));
    }
}
