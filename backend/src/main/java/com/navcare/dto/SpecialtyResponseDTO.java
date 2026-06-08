package com.navcare.dto;

import lombok.Data;

@Data
public class SpecialtyResponseDTO {

    // Aqui eu devolvo somente os campos que o frontend precisa para listar e editar a especialidade.
    private Long id;
    private String name;
    private String description;
}
