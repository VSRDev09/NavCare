package com.navcare.integration.ai;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navcare.config.OpenAiProperties;
import com.navcare.exception.AiIntegrationException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private static final String JSON_SCHEMA_NAME = "triage_response";

    private final RestClient restClient;
    private final OpenAiProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyze(String prompt, String report) {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new AiIntegrationException("Chave da OpenAI API não configurada.");
        }

        String url = properties.getBaseUrl() + "/v1/responses";
        Map<String, Object> body = Map.of(
            "model", properties.getModel(),
            "instructions", prompt,
            "input", report,
            "text", Map.of(
                "format", Map.of(
                    "type", "json_schema",
                    "name", JSON_SCHEMA_NAME,
                    "schema", Map.of(
                        "type", "object",
                        "additionalProperties", false,
                        "properties", Map.of(
                            "specialty", Map.of("type", "string"),
                            "urgency", Map.of("type", "string", "enum", List.of("Baixa", "Média", "Alta")),
                            "summary", Map.of("type", "string")
                        ),
                        "required", List.of("specialty", "urgency", "summary")
                    ),
                    "strict", true
                )
            ),
            "max_output_tokens", 300
        );

        try {
            String response = restClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

            if (response == null || response.isBlank()) {
                throw new AiIntegrationException("A IA retornou uma resposta vazia.");
            }

            return extractOutputText(response);
        } catch (Exception exception) {
            if (exception instanceof AiIntegrationException aiIntegrationException) {
                throw aiIntegrationException;
            }
            throw new AiIntegrationException("Falha ao comunicar com a IA.", exception);
        }
    }

    private String extractOutputText(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode output = root.path("output");
            if (output.isArray()) {
                for (JsonNode outputItem : output) {
                    JsonNode content = outputItem.path("content");
                    if (content.isArray()) {
                        for (JsonNode contentItem : content) {
                            JsonNode textNode = contentItem.path("text");
                            if (!textNode.isMissingNode() && !textNode.asText().isBlank()) {
                                return textNode.asText();
                            }
                        }
                    }
                }
            }

            if (root.hasNonNull("output_text")) {
                return root.path("output_text").asText();
            }

            return response;
        } catch (Exception exception) {
            throw new AiIntegrationException("Resposta da OpenAI inválida.", exception);
        }
    }
}
