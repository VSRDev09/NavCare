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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class GeminiClient {

    private final RestClient restClient;
    private final GeminiProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyze(String prompt, String report) {

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
                        "responseMimeType", "application/json",
                        "responseSchema", Map.of(
                                "type", "OBJECT",
                                "properties", Map.of(
                                        "specialty", Map.of("type", "STRING"),
                                        "urgency", Map.of(
                                                "type", "STRING",
                                                "enum", List.of("Baixa", "Média", "Alta")),
                                        "summary", Map.of("type", "STRING")),
                                "required", List.of("specialty", "urgency", "summary")),
                        "temperature", 0.2,
                        "maxOutputTokens", 300));

        try {
            String response = callGemini(url, body);
            log.info("Prompt enviado para Gemini: {}", prompt);
            log.info("Report enviado para Gemini: {}", report);
            log.info("Resposta bruta da Gemini:\n{}", response);

            if (response == null || response.isBlank()) {
                throw new AiIntegrationException("A IA retornou uma resposta vazia.");
            }

            return extractOutputText(response);

        } catch (RestClientResponseException exception) {

            String responseBody = limit(exception.getResponseBodyAsString(), 2000);

            log.warn(
                    "Erro HTTP ao chamar a Gemini. status={}, statusText={}, body={}",
                    exception.getStatusCode().value(),
                    exception.getStatusText(),
                    responseBody);

            throw new AiIntegrationException(
                    "Falha ao comunicar com a IA. HTTP "
                            + exception.getStatusCode().value()
                            + " - " + exception.getStatusText()
                            + ". Resposta: " + responseBody,
                    exception);

        } catch (ResourceAccessException exception) {

            log.warn("Erro de rede ao chamar a Gemini. Motivo: {}", exception.getMessage(), exception);

            throw new AiIntegrationException(
                    "Falha ao comunicar com a IA. Erro de rede: " + exception.getMessage(),
                    exception);

        } catch (Exception exception) {

            if (exception instanceof AiIntegrationException aiEx) {
                throw aiEx;
            }

            log.warn(
                    "Erro inesperado ao chamar a Gemini. Tipo={}, Motivo={}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    exception);

            throw new AiIntegrationException("Falha ao comunicar com a IA.", exception);
        }
    }

    private String callGemini(String url, Map<String, Object> body) {

        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return restClient.post()
                        .uri(url)
                        .header("x-goog-api-key", properties.getApiKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(String.class);

            } catch (org.springframework.web.client.HttpServerErrorException.ServiceUnavailable e) {

                if (attempt == maxAttempts) {
                    throw new AiIntegrationException(
                            "Gemini indisponível após retries (503).",
                            e);
                }

                long waitMs = (long) Math.pow(2, attempt) * 1000;

                log.warn(
                        "Gemini 503 (UNAVAILABLE). Tentativa {}/{}. Retry em {} ms",
                        attempt,
                        maxAttempts,
                        waitMs);

                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new AiIntegrationException(
                            "Thread interrompida durante retry da Gemini.",
                            ie);
                }
            }
        }

        throw new AiIntegrationException("Falha inesperada no retry da Gemini.");
    }

    private String extractOutputText(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.path("candidates");

            if (candidates.isArray()) {
                for (JsonNode candidate : candidates) {
                    JsonNode content = candidate.path("content");
                    JsonNode parts = content.path("parts");

                    if (parts.isArray()) {
                        for (JsonNode part : parts) {
                            JsonNode text = part.path("text");
                            if (!text.isMissingNode() && !text.asText().isBlank()) {
                                return text.asText();
                            }
                        }
                    }
                }
            }

            return response;

        } catch (Exception exception) {
            throw new AiIntegrationException("Resposta da Gemini inválida.", exception);
        }
    }

    private String limit(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String normalized = value.strip().replaceAll("\\s+", " ");

        return normalized.length() <= maxLength
                ? normalized
                : normalized.substring(0, maxLength) + "...";
    }
}