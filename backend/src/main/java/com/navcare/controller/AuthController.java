package com.navcare.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.navcare.dto.auth.LoginRequestDTO;
import com.navcare.dto.auth.LoginResponseDTO;
import com.navcare.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        // Eu deixo este login publico porque ele e o unico ponto de entrada do painel administrativo.
        return ResponseEntity.ok(authService.login(request));
    }
}
