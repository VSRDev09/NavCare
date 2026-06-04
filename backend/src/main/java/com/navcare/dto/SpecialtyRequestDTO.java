package com.navcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SpecialtyRequestDTO {

    @NotBlank(message = "O nome da especialidade é obrigatório.")
    @Size(max = 120, message = "O nome da especialidade deve ter no máximo 120 caracteres.")
    private String name;

    @NotBlank(message = "A descrição da especialidade é obrigatória.")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    private String description;
}
