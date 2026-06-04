package com.navcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AttendanceRuleRequestDTO {

    @NotNull(message = "O tempo médio de espera é obrigatório.")
    @Positive(message = "O tempo médio de espera deve ser maior que zero.")
    private Integer averageWaitTime;

    @NotNull(message = "O campo aceita emergência é obrigatório.")
    private Boolean acceptsEmergency;

    @NotBlank(message = "As observações são obrigatórias.")
    @Size(max = 500, message = "As observações devem ter no máximo 500 caracteres.")
    private String notes;

    @NotNull(message = "A especialidade é obrigatória.")
    private Long specialtyId;
}
