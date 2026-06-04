package com.navcare.service;

import java.time.Instant;

import com.navcare.dto.auth.LoginRequestDTO;
import com.navcare.dto.auth.LoginResponseDTO;
import com.navcare.entity.Admin;
import com.navcare.integration.security.JwtService;
import com.navcare.exception.UnauthorizedException;
import com.navcare.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        Admin admin = adminRepository.findByUsername(request.getUsername().trim())
            .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas."));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new UnauthorizedException("Credenciais inválidas.");
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(jwtService.getExpirationSeconds());

        String token = jwtService.generateToken(admin.getUsername(), issuedAt, expiresAt);
        return LoginResponseDTO.builder()
            .token(token)
            .type("Bearer")
            .username(admin.getUsername())
            .expiresInSeconds(jwtService.getExpirationSeconds())
            .build();
    }
}
