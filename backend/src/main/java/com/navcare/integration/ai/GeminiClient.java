package com.navcare.integration.ai;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navcare.config.GeminiProperties;
import com.navcare.exception.AiIntegrationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Component
@Slf4j
@RequiredArgsConstructor
public class GeminiClient {

    private final RestClient restClient;
    private final GeminiProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyze(String prompt, String report) {

        log.info("========================================");
        log.info("=== ANALYZE ===");
        log.info("BASE URL: {}", properties.getBaseUrl());
        log.info("MODEL: {}", properties.getModel());
        
        String apiKey = properties.getApiKey();
        
        if (apiKey != null && apiKey.length() >= 8) {
            log.info("API KEY PREFIX: {}", apiKey.substring(0, 8));
        }
        
        log.info("========================================");

        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new AiIntegrationException("Chave da Gemini API não configurada.");
        }

        String url = properties.getBaseUrl()
                + "/v1beta/models/"
                + properties.getModel()
                + ":generateContent";

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "maxOutputTokens", 800,
                        "responseMimeType", "application/json",
                        "thinkingConfig", Map.of(
                                "thinkingBudget", 120)));

        try {
            String response = callGemini(url, body);

            log.info("Prompt enviado:\n{}", prompt);
            log.info("Report enviado:\n{}", report);
            log.info("Resposta bruta Gemini:\n{}", response);

            if (response == null || response.isBlank()) {
                throw new AiIntegrationException("IA retornou resposta vazia.");
            }

            return extractOutputText(response);

        } catch (Exception e) {
            log.warn("Falha ao chamar Gemini. Tipo={}, Motivo={}",
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e);

            throw new AiIntegrationException("Falha ao comunicar com a IA.", e);
        }
    }


    private String callGemini(String url, Map<String, Object> body) {

    try {

        log.info("========================================");
        log.info("=== CHAMANDO GEMINI ===");
        log.info("URL: {}", url);
        log.info("BASE URL: {}", properties.getBaseUrl());
        log.info("MODEL: {}", properties.getModel());

        String apiKey = properties.getApiKey();

        if (apiKey != null && apiKey.length() >= 8) {
            log.info("API KEY PREFIX: {}", apiKey.substring(0, 8));
        }

        String response = restClient.post()
                .uri(url)
                .header("x-goog-api-key", properties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        log.info("=== GEMINI SUCCESS ===");

        return response;

    } catch (HttpClientErrorException e) {

        log.error("========================================");
        log.error("=== GEMINI CLIENT ERROR ===");
        log.error("TYPE: {}", e.getClass().getName());
        log.error("STATUS: {}", e.getStatusCode());
        log.error("BODY: {}", e.getResponseBodyAsString());
        log.error("========================================");

        throw e;

    } catch (HttpServerErrorException e) {

        log.error("========================================");
        log.error("=== GEMINI SERVER ERROR ===");
        log.error("TYPE: {}", e.getClass().getName());
        log.error("STATUS: {}", e.getStatusCode());
        log.error("BODY: {}", e.getResponseBodyAsString());
        log.error("========================================");

        throw e;

    } catch (Exception e) {

        log.error("========================================");
        log.error("=== GEMINI UNKNOWN ERROR ===");
        log.error("TYPE: {}", e.getClass().getName());
        log.error("MESSAGE: {}", e.getMessage(), e);
        log.error("========================================");

        throw e;
    }
}

    private String extractOutputText(String response) {

        try {
            JsonNode root = objectMapper.readTree(response);

            JsonNode candidates = root.path("candidates");

            if (candidates.isArray()) {
                for (JsonNode candidate : candidates) {

                    JsonNode parts = candidate
                            .path("content")
                            .path("parts");

                    if (parts.isArray()) {
                        for (JsonNode part : parts) {

                            String text = part.path("text").asText("");

                            if (!text.isBlank()) {

                                log.debug("Texto bruto da IA: {}", text);

                                return cleanJson(text);
                            }
                        }
                    }
                }
            }

            throw new AiIntegrationException("Resposta da Gemini sem conteúdo válido.");

        } catch (Exception e) {
            throw new AiIntegrationException("Resposta da Gemini inválida.", e);
        }
    }

    private String cleanJson(String text) {

        String cleaned = text.trim();

        cleaned = cleaned.replaceAll("(?s)```json", "");
        cleaned = cleaned.replaceAll("```", "");

        int start = cleaned.indexOf("{");
        int end = cleaned.lastIndexOf("}");

        if (start >= 0 && end > start) {
            return cleaned.substring(start, end + 1);
        }

        return cleaned;
    }
}
