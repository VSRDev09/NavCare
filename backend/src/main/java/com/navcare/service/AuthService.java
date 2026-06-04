package com.navcare.service;

import java.time.Instant;
import java.util.Objects;

import com.navcare.config.SecurityProperties;
import com.navcare.dto.auth.LoginRequestDTO;
import com.navcare.dto.auth.LoginResponseDTO;
import com.navcare.integration.security.JwtService;
import com.navcare.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SecurityProperties securityProperties;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        if (!Objects.equals(securityProperties.getAdminUsername(), request.getUsername())
            || !Objects.equals(securityProperties.getAdminPassword(), request.getPassword())) {
            throw new UnauthorizedException("Credenciais inválidas.");
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(jwtService.getExpirationSeconds());

        String token = jwtService.generateToken(securityProperties.getAdminUsername(), issuedAt, expiresAt);
        return LoginResponseDTO.builder()
            .token(token)
            .type("Bearer")
            .username(securityProperties.getAdminUsername())
            .expiresInSeconds(jwtService.getExpirationSeconds())
            .build();
    }
}
