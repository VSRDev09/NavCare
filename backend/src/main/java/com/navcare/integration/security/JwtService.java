package com.navcare.integration.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.navcare.config.SecurityProperties;
import com.navcare.exception.AiIntegrationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final SecurityProperties securityProperties;

    public String generateToken(String username, Instant issuedAt, Instant expiresAt) {
        // Aqui eu gero um token enxuto, com as claims minimas que eu preciso
        // para manter o contrato simples e nao acoplar o frontend a detalhes internos.
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
            .issuer("Nav.Care")
            .subject(username)
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .claim("roles", List.of("ROLE_ADMIN"))
            .id(UUID.randomUUID().toString())
            .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }

    public long getExpirationSeconds() {
        // Eu centralizo esta conversao aqui para nao repetir o calculo em outras camadas.
        Integer minutes = securityProperties.getJwtExpirationMinutes() != null ? securityProperties.getJwtExpirationMinutes() : 480;
        return minutes.longValue() * 60L;
    }

    public SecretKey secretKey() {
        // Eu derivo a chave textual para bytes fixos para manter compatibilidade com HS256.
        String jwtSecret = securityProperties.getJwtSecret();
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new AiIntegrationException("JWT_SECRET não configurado.");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(keyBytes, "HmacSHA256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Falha ao gerar chave JWT.", exception);
        }
    }
}
