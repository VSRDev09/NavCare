package com.navcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(GeminiProperties properties) {
        // Eu uso timeout configuravel porque a chamada externa nao pode travar a triagem.
        int timeoutSeconds = properties.getTimeoutSeconds() != null ? properties.getTimeoutSeconds() : 20;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutSeconds * 1000);
        requestFactory.setReadTimeout(timeoutSeconds * 1000);

        return RestClient.builder()
            .requestFactory(requestFactory)
            .build();
    }
}
