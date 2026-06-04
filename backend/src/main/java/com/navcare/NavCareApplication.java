package com.navcare;

import com.navcare.config.OpenAiProperties;
import com.navcare.config.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({OpenAiProperties.class, SecurityProperties.class})
public class NavCareApplication {

    public static void main(String[] args) {
        int javaFeatureVersion = Runtime.version().feature();
        if (javaFeatureVersion != 25) {
            throw new IllegalStateException(
                "Nav.Care requer Java 25. Versão detectada: " + javaFeatureVersion +
                ". Ajuste o JAVA_HOME para uma instalação do JDK 25 antes de iniciar a aplicação."
            );
        }
        SpringApplication.run(NavCareApplication.class, args);
    }
}
