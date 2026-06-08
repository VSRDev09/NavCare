package com.navcare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "navcare.ai.gemini")
public class GeminiProperties {

    // Eu deixo estas propriedades fora do codigo para poder trocar chave,
    // modelo, base URL e timeout sem recompilar a aplicacao.
    private String apiKey;
    private String baseUrl;
    private String model;
    private Integer timeoutSeconds;
}
