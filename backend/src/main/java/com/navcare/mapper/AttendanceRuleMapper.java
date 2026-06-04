package com.navcare.mapper;

import com.navcare.dto.AttendanceRuleRequestDTO;
import com.navcare.dto.AttendanceRuleResponseDTO;
import com.navcare.dto.AttendanceRuleSummaryDTO;
import com.navcare.entity.AttendanceRule;
import com.navcare.entity.Specialty;

public final class AttendanceRuleMapper {

    private AttendanceRuleMapper() {
    }

    public static AttendanceRule toEntity(AttendanceRuleRequestDTO dto, Specialty specialty) {
        return AttendanceRule.builder()
            .averageWaitTime(dto.getAverageWaitTime())
            .acceptsEmergency(dto.getAcceptsEmergency())
            .notes(dto.getNotes().trim())
            .specialty(specialty)
            .build();
    }

    public static AttendanceRuleResponseDTO toResponseDTO(AttendanceRule entity) {
        AttendanceRuleResponseDTO dto = new AttendanceRuleResponseDTO();
        dto.setId(entity.getId());
        dto.setAverageWaitTime(entity.getAverageWaitTime());
        dto.setAcceptsEmergency(entity.getAcceptsEmergency());
        dto.setNotes(entity.getNotes());
        dto.setSpecialtyName(entity.getSpecialty().getName());
        return dto;
    }

    public static AttendanceRuleSummaryDTO toSummaryDTO(AttendanceRule entity) {
        AttendanceRuleSummaryDTO dto = new AttendanceRuleSummaryDTO();
        dto.setAverageWaitTime(entity.getAverageWaitTime());
        dto.setAcceptsEmergency(entity.getAcceptsEmergency());
        dto.setNotes(entity.getNotes());
        return dto;
    }

    public static void updateEntity(AttendanceRule entity, AttendanceRuleRequestDTO dto, Specialty specialty) {
        entity.setAverageWaitTime(dto.getAverageWaitTime());
        entity.setAcceptsEmergency(dto.getAcceptsEmergency());
        entity.setNotes(dto.getNotes().trim());
        entity.setSpecialty(specialty);
    }
}
