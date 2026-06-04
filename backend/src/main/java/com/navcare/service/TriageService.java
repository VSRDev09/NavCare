package com.navcare.service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navcare.dto.AiTriageResultDTO;
import com.navcare.dto.AttendanceRuleSummaryDTO;
import com.navcare.dto.TriageRequestDTO;
import com.navcare.dto.TriageResponseDTO;
import com.navcare.entity.AttendanceRule;
import com.navcare.entity.Specialty;
import com.navcare.exception.AiIntegrationException;
import com.navcare.exception.ResourceNotFoundException;
import com.navcare.integration.ai.OpenAiClient;
import com.navcare.mapper.AttendanceRuleMapper;
import com.navcare.repository.AttendanceRuleRepository;
import com.navcare.repository.SpecialtyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TriageService {

    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
    private static final String DEFAULT_SPECIALTY_NAME = "Clínica Geral";

    private final SpecialtyRepository specialtyRepository;
    private final AttendanceRuleRepository attendanceRuleRepository;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public TriageResponseDTO triage(TriageRequestDTO request) {
        List<Specialty> specialties = specialtyRepository.findAll().stream()
            .sorted(Comparator.comparing(Specialty::getName))
            .toList();

        AiTriageResultDTO aiResult = analyzeWithAiOrFallback(request.getReport(), specialties);
        Specialty resolvedSpecialty = resolveSpecialty(aiResult.getSpecialty(), specialties);
        List<AttendanceRuleSummaryDTO> attendanceRules = attendanceRuleRepository.findBySpecialty_Id(resolvedSpecialty.getId()).stream()
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(AttendanceRule::getId))
            .map(AttendanceRuleMapper::toSummaryDTO)
            .toList();

        TriageResponseDTO response = new TriageResponseDTO();
        response.setSpecialty(resolvedSpecialty.getName());
        response.setUrgency(normalizeUrgency(aiResult.getUrgency()));
        response.setSummary(aiResult.getSummary());
        response.setAttendanceRules(attendanceRules);
        return response;
    }

    private AiTriageResultDTO analyzeWithAiOrFallback(String report, List<Specialty> specialties) {
        try {
            String prompt = buildPrompt(report, specialties);
            String rawResponse = openAiClient.analyze(prompt, report);
            return parseAiResponse(rawResponse);
        } catch (Exception exception) {
            log.warn("Falha na IA. Usando fallback local para a triagem. Motivo: {}", exception.getMessage());
            return localFallbackAnalysis(report, specialties);
        }
    }

    private String buildPrompt(String report, List<Specialty> specialties) {
        String specialtiesList = specialties.isEmpty()
            ? "- Clínica Geral"
            : specialties.stream()
                .map(specialty -> "- " + specialty.getName())
                .reduce((left, right) -> left + "\n" + right)
                .orElse("- Clínica Geral");

        return """
            Você é um sistema de triagem médica do Nav.Care.

            Analise o relato abaixo com cuidado e empatia.

            Você DEVE escolher apenas UMA das especialidades abaixo:

            %s

            Não invente especialidades.
            Classifique a urgência apenas como:
            - Baixa
            - Média
            - Alta

            Retorne APENAS JSON.

            Formato obrigatório:
            {
              "specialty": "",
              "urgency": "",
              "summary": ""
            }

            Especialidades permitidas:
            %s

            Relato do paciente:
            %s
            """.formatted(specialtiesList, specialtiesList, report);
    }

    private AiTriageResultDTO parseAiResponse(String rawResponse) {
        try {
            String json = extractJson(rawResponse);
            AiTriageResultDTO dto = objectMapper.readValue(json, AiTriageResultDTO.class);
            if (dto.getSpecialty() == null || dto.getUrgency() == null || dto.getSummary() == null) {
                throw new AiIntegrationException("A IA retornou um JSON incompleto.");
            }
            return dto;
        } catch (Exception exception) {
            throw new AiIntegrationException("Resposta da IA inválida.", exception);
        }
    }

    private String extractJson(String rawResponse) {
        Matcher matcher = JSON_OBJECT_PATTERN.matcher(rawResponse);
        if (matcher.find()) {
            return matcher.group();
        }
        return rawResponse;
    }

    private AiTriageResultDTO localFallbackAnalysis(String report, List<Specialty> specialties) {
        AiTriageResultDTO dto = new AiTriageResultDTO();
        dto.setSpecialty(resolveLocalSpecialty(report, specialties));
        dto.setUrgency(resolveLocalUrgency(report));
        dto.setSummary(buildSummary(report));
        return dto;
    }

    private String resolveLocalSpecialty(String report, List<Specialty> specialties) {
        String normalizedReport = normalize(report);

        if (containsAny(normalizedReport, "peito", "coração", "palpita", "pressão", "cardio")) {
            return findSpecialtyOrDefault("Cardiologia", specialties);
        }

        if (containsAny(normalizedReport, "cabeça", "cefaleia", "enxaqueca", "visão", "vista", "neurol", "convuls", "tontura", "formig", "fraqueza")) {
            return findSpecialtyOrDefault("Neurologia", specialties);
        }

        if (containsAny(normalizedReport, "osso", "fratura", "joelho", "ombro", "coluna", "tornozelo", "ortop", "músculo", "musculo")) {
            return findSpecialtyOrDefault("Ortopedia", specialties);
        }

        return findSpecialtyOrDefault(DEFAULT_SPECIALTY_NAME, specialties);
    }

    private String resolveLocalUrgency(String report) {
        String normalizedReport = normalize(report);
        if (containsAny(normalizedReport, "desma", "falta de ar", "dor no peito", "sangramento", "convuls", "visão embaçada", "visao embaçada", "muito forte", "intensa")) {
            return "Alta";
        }
        if (containsAny(normalizedReport, "febre", "dor", "mal-estar", "mal estar", "náuse", "nause", "tontura", "incha", "tosse")) {
            return "Média";
        }
        return "Baixa";
    }

    private String buildSummary(String report) {
        String normalized = report.strip().replaceAll("\\s+", " ");
        String shortText = normalized.length() > 180 ? normalized.substring(0, 180) + "..." : normalized;
        return "Paciente relata: " + shortText;
    }

    private Specialty resolveSpecialty(String specialtyName, List<Specialty> specialties) {
        String normalizedTarget = normalize(specialtyName);

        return specialties.stream()
            .filter(specialty -> normalize(specialty.getName()).equals(normalizedTarget))
            .findFirst()
            .orElseGet(() -> {
                log.warn("Especialidade '{}' não encontrada. Aplicando fallback para Clínica Geral.", specialtyName);
                return specialties.stream()
                    .filter(specialty -> normalize(specialty.getName()).equals(normalize(DEFAULT_SPECIALTY_NAME)))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Especialidade de fallback não encontrada no banco."));
            });
    }

    private String findSpecialtyOrDefault(String target, List<Specialty> specialties) {
        return specialties.stream()
            .filter(specialty -> normalize(specialty.getName()).equals(normalize(target)))
            .map(Specialty::getName)
            .findFirst()
            .orElseGet(() -> specialties.stream()
                .filter(specialty -> normalize(specialty.getName()).equals(normalize(DEFAULT_SPECIALTY_NAME)))
                .map(Specialty::getName)
                .findFirst()
                .orElse(DEFAULT_SPECIALTY_NAME));
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(normalize(keyword))) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT);
    }

    private String normalizeUrgency(String urgency) {
        String normalized = normalize(urgency);
        if (normalized.contains("alta")) {
            return "Alta";
        }
        if (normalized.contains("media")) {
            return "Média";
        }
        if (normalized.contains("baixa")) {
            return "Baixa";
        }
        return "Baixa";
    }
}
