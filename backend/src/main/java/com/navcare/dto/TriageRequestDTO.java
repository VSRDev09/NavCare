package com.navcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TriageRequestDTO {

    @NotBlank(message = "O relato do paciente é obrigatório.")
    @Size(max = 4000, message = "O relato deve ter no máximo 4000 caracteres.")
    private String report;
}
