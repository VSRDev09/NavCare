package com.navcare.dto;

import lombok.Data;

@Data
public class AttendanceRuleSummaryDTO {

    // Aqui eu envio apenas o que a tela precisa para exibir a regra sem expor o modelo completo.
    private Integer averageWaitTime;
    private Boolean acceptsEmergency;
    private String notes;
}
