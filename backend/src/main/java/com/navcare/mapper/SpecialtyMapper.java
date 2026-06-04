package com.navcare.mapper;

import com.navcare.dto.SpecialtyRequestDTO;
import com.navcare.dto.SpecialtyResponseDTO;
import com.navcare.entity.Specialty;

public final class SpecialtyMapper {

    private SpecialtyMapper() {
    }

    public static Specialty toEntity(SpecialtyRequestDTO dto) {
        return Specialty.builder()
            .name(dto.getName().trim())
            .description(dto.getDescription().trim())
            .build();
    }

    public static SpecialtyResponseDTO toResponseDTO(Specialty entity) {
        SpecialtyResponseDTO dto = new SpecialtyResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    public static void updateEntity(Specialty entity, SpecialtyRequestDTO dto) {
        entity.setName(dto.getName().trim());
        entity.setDescription(dto.getDescription().trim());
    }
}
