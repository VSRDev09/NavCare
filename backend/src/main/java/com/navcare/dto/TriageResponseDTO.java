package com.navcare.dto;

import java.util.List;

import lombok.Data;

@Data
public class TriageResponseDTO {

    // Aqui eu mantenho a resposta da triagem enxuta para o frontend nao precisar conhecer as entidades internas.
    private String specialty;
    private String urgency;
    private String summary;
    private List<AttendanceRuleSummaryDTO> attendanceRules;
}
