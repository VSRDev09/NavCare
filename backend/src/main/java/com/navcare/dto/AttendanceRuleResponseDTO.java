package com.navcare.dto;

import lombok.Data;

@Data
public class AttendanceRuleResponseDTO {

    // Aqui eu retorno o detalhe necessario para a tela administrativa, incluindo o nome da especialidade vinculada.
    private Long id;
    private Integer averageWaitTime;
    private Boolean acceptsEmergency;
    private String notes;
    private String specialtyName;
}
