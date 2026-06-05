package com.navcare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "navcare.ai.gemini")
public class GeminiProperties {

    private String apiKey;
    private String baseUrl;
    private String model;
    private Integer timeoutSeconds;
}
