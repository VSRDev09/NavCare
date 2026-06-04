package com.navcare.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.navcare.dto.AttendanceRuleRequestDTO;
import com.navcare.dto.AttendanceRuleResponseDTO;
import com.navcare.service.AttendanceRuleService;

@RestController
@RequestMapping("/api/attendance-rules")
@RequiredArgsConstructor
public class AttendanceRuleController {

    private final AttendanceRuleService attendanceRuleService;

    @GetMapping
    public ResponseEntity<List<AttendanceRuleResponseDTO>> findAll() {
        return ResponseEntity.ok(attendanceRuleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceRuleResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceRuleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AttendanceRuleResponseDTO> create(@Valid @RequestBody AttendanceRuleRequestDTO dto) {
        AttendanceRuleResponseDTO response = attendanceRuleService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.getId())
            .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceRuleResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AttendanceRuleRequestDTO dto) {
        return ResponseEntity.ok(attendanceRuleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attendanceRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
