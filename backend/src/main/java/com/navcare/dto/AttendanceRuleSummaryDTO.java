package com.navcare.dto;

import lombok.Data;

@Data
public class AttendanceRuleSummaryDTO {

    private Integer averageWaitTime;
    private Boolean acceptsEmergency;
    private String notes;
}
