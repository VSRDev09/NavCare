package com.navcare.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "O usuário é obrigatório.")
    // Aqui eu recebo apenas as credenciais minimas para autenticar o administrador.
    private String username;

    @NotBlank(message = "A senha é obrigatória.")
    private String password;
}
