package com.navcare.dto;

import lombok.Data;

@Data
public class AttendanceRuleResponseDTO {

    private Long id;
    private Integer averageWaitTime;
    private Boolean acceptsEmergency;
    private String notes;
    private String specialtyName;
}
