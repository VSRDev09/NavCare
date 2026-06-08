package com.navcare.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${navcare.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Eu mantenho o CORS flexivel porque o front roda tanto localmente
        // quanto em container, e eu nao quero travar a navegacao por origem fixa.
        List<String> origins = List.of(allowedOrigins.split(",")).stream()
            .map(String::trim)
            .filter(origin -> !origin.isBlank())
            .toList();
        registry.addMapping("/**")
            .allowedOriginPatterns(origins.isEmpty() ? new String[] {"*"} : origins.toArray(String[]::new))
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("*");
    }
}
