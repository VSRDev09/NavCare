package com.navcare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "navcare.security")
public class SecurityProperties {

    private String jwtSecret;
    private Integer jwtExpirationMinutes;
}
