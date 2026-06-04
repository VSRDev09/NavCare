package com.navcare.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.navcare.dto.TriageRequestDTO;
import com.navcare.dto.TriageResponseDTO;
import com.navcare.service.TriageService;

@RestController
@RequestMapping("/api/triage")
@RequiredArgsConstructor
public class TriageController {

    private final TriageService triageService;

    @PostMapping
    public ResponseEntity<TriageResponseDTO> triage(@Valid @RequestBody TriageRequestDTO request) {
        return ResponseEntity.ok(triageService.triage(request));
    }
}
