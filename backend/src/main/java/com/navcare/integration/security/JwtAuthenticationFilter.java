package com.navcare.integration.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

       // Eu mantive estes logs para entender rapidamente o caminho de cada requisicao
       // durante o debug, principalmente quando eu preciso separar rota publica de rota protegida.
       System.out.println("REQUEST => " + request.getMethod() + " URI=" + request.getRequestURI() +
                          " URL=" + request.getRequestURL() +
                            " SERVLET=" + request.getServletPath()
                            );

       System.out.println("AUTH => " + request.getHeader(HttpHeaders.AUTHORIZATION));
            
        // Eu so tento autenticar quando existe um Bearer token; se nao existir,
        // eu deixo a requisicao seguir para nao quebrar as rotas publicas.
        String token = extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                jwt.getSubject(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException exception) {
            // Se o token estiver invalido, eu limpo o contexto para nao deixar uma autenticacao parcial
            // contaminar a decisao de acesso da requisicao.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }
        if (!authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        // Eu removo apenas o prefixo porque quero reaproveitar o token puro na etapa de decode.
        String token = authorizationHeader.substring(7).trim();
        return token.isBlank() ? null : token;
    }
}
