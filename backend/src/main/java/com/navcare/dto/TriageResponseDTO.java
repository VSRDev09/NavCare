package com.navcare.dto;

import java.util.List;

import lombok.Data;

@Data
public class TriageResponseDTO {

    private String specialty;
    private String urgency;
    private String summary;
    private List<AttendanceRuleSummaryDTO> attendanceRules;
}
