package com.navcare.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {

    // Aqui eu retorno somente o necessario para o front guardar a sessao e redirecionar o admin.
    private String token;
    private String type;
    private String username;
    private Long expiresInSeconds;
}
