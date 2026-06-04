package com.navcare.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI navCareOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Nav.Care")
                .description("Nav.Care - Sistema de Triagem e Navegação Inteligente")
                .version("1.0.0")
                .contact(new Contact().name("Nav.Care")));
    }
}
