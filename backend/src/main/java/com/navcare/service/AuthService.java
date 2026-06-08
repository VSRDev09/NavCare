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

    // Aqui eu mantenho a origem das credenciais no banco para evitar usuario ou senha fixos no codigo.
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Aqui eu concentro o login administrativo em um unico metodo para manter
    // a validacao do banco, do BCrypt e do JWT no mesmo fluxo de decisao.
    public LoginResponseDTO login(LoginRequestDTO request) {
        // Aqui eu busco o administrador no banco para evitar credenciais fixas
        // no .env e manter o controle de acesso dentro da propria aplicacao.
        Admin admin = adminRepository.findByUsername(request.getUsername().trim())
            .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas."));

        // Eu nunca comparo senha em texto puro; eu dependo do BCrypt para validar a credencial.
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new UnauthorizedException("Credenciais inválidas.");
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(jwtService.getExpirationSeconds());

        // Eu gero o token somente depois que a identidade foi confirmada com sucesso.
        String token = jwtService.generateToken(admin.getUsername(), issuedAt, expiresAt);
        return LoginResponseDTO.builder()
            .token(token)
            .type("Bearer")
            .username(admin.getUsername())
            .expiresInSeconds(jwtService.getExpirationSeconds())
            .build();
    }
}
