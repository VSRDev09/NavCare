package com.navcare.service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navcare.dto.*;
import com.navcare.entity.Specialty;
import com.navcare.exception.AiIntegrationException;
import com.navcare.exception.ResourceNotFoundException;
import com.navcare.integration.ai.GeminiClient;
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

    private static final String DEFAULT_SPECIALTY_NAME = "Clínica Geral";

    // Aqui eu concentro toda a orquestracao da triagem para manter o controller fino
    // e deixar claro onde a IA, o fallback e as regras do banco se encontram.
    private final SpecialtyRepository specialtyRepository;
    private final AttendanceRuleRepository attendanceRuleRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public TriageResponseDTO triage(TriageRequestDTO request) {

        // Aqui eu busco as especialidades reais do banco para que a IA e a resposta final
        // trabalhem sempre com o mesmo catalogo mantido pelo administrador.
        List<Specialty> specialties = specialtyRepository.findAll().stream()
                .sorted(Comparator.comparing(Specialty::getName))
                .toList();

        AiTriageResultDTO aiResult = analyzeWithAiOrFallback(request.getReport(), specialties);

        Specialty resolved = resolveSpecialty(aiResult.getSpecialty(), specialties);

        List<AttendanceRuleSummaryDTO> rules = attendanceRuleRepository
                .findBySpecialty_Id(resolved.getId())
                .stream()
                .filter(Objects::nonNull)
                .map(AttendanceRuleMapper::toSummaryDTO)
                .map(rule -> applyUrgencyToWaitTime(rule, normalizeUrgency(aiResult.getUrgency())))
                .toList();

        TriageResponseDTO response = new TriageResponseDTO();
        response.setSpecialty(resolved.getName());
        response.setUrgency(normalizeUrgency(aiResult.getUrgency()));
        response.setSummary(aiResult.getSummary());
        response.setAttendanceRules(rules);

        return response;
    }

    private AiTriageResultDTO analyzeWithAiOrFallback(String report, List<Specialty> specialties) {
        try {
            // Eu prefiro tentar a IA primeiro e cair no fallback so quando houver falha,
            // porque a resposta inteligente precisa ser o caminho principal do sistema.
            String prompt = buildPrompt(report, specialties);
            String raw = geminiClient.analyze(prompt, report);

            return parseAiResponse(raw);

        } catch (Exception e) {
            log.warn("Falha na IA. Usando fallback local. Motivo={}", e.getMessage(), e);
            return localFallbackAnalysis(report, specialties);
        }
    }

    
    private String buildPrompt(String report, List<Specialty> specialties) {

        // Eu monto o prompt com especialidades reais do banco para manter a IA presa
        // ao catalogo atual do Nav.Care.
        String specialtiesList = specialties.stream()
                .map(s -> "- " + s.getName())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("- Clínica Geral");

        return """
                Você é um sistema de triagem médica.

                REGRAS OBRIGATÓRIAS:
                - Responda SOMENTE com JSON válido
                - NÃO use markdown
                - NÃO escreva texto antes ou depois
                - NÃO explique nada

                Seja direto e conciso no seu raciocínio. Não faça análises profundas

                FORMATO OBRIGATÓRIO:
                {
                  "specialty": "",
                  "urgency": "Baixa | Média | Alta",
                  "summary": ""
                }

                REGRAS DO SUMMARY:
                - máximo 20 palavras
                - uma única frase
                - não explicar diagnóstico
                - não sugerir tratamento

                Especialidades permitidas:
                %s

                Relato do paciente:
                %s
                """.formatted(specialtiesList, report);
    }

  
    private AiTriageResultDTO parseAiResponse(String raw) {
        try {
            // Eu valido o JSON antes de misturar a resposta da IA com dados do banco,
            // porque um payload parcial nao pode contaminar a triagem final.
            String json = extractJsonSafe(raw);

            AiTriageResultDTO dto = objectMapper.readValue(json, AiTriageResultDTO.class);

            if (dto.getSpecialty() == null ||
                    dto.getUrgency() == null ||
                    dto.getSummary() == null) {
                throw new AiIntegrationException("JSON incompleto da IA.");
            }

            return dto;

        } catch (Exception e) {
            throw new AiIntegrationException("Falha ao interpretar resposta da IA.", e);
        }
    }

  
    private String extractJsonSafe(String response) {

        // Eu extraio apenas o primeiro objeto JSON porque a IA pode devolver texto extra,
        // markdown ou uma resposta parcialmente truncada.
        if (response == null || response.isBlank()) {
            throw new AiIntegrationException("Resposta vazia da IA.");
        }

        int start = response.indexOf('{');

        if (start == -1) {
            throw new AiIntegrationException("JSON não encontrado na resposta da IA.");
        }

        StringBuilder json = new StringBuilder();
        int braces = 0;
        boolean started = false;

        for (int i = start; i < response.length(); i++) {
            char c = response.charAt(i);

            if (c == '{') {
                braces++;
                started = true;
            }

            if (started) {
                json.append(c);
            }

            if (c == '}') {
                braces--;
            }

           
            if (started && braces == 0) {
                return json.toString();
            }
        }

        // Caso típico do erro (MAX_TOKENS truncou JSON)
        // Eu trato isso como truncamento porque o objeto nao fechou corretamente.
        throw new AiIntegrationException(
                "JSON veio truncado pela IA (MAX_TOKENS). Resposta parcial: " + json);
    }

 
    private AiTriageResultDTO localFallbackAnalysis(String report, List<Specialty> specialties) {

        // Eu mantenho uma resposta local basica para que a triagem nunca fique vazia
        // quando a IA estiver indisponivel.
        AiTriageResultDTO dto = new AiTriageResultDTO();

        dto.setSpecialty(resolveLocalSpecialty(report, specialties));
        dto.setUrgency(resolveLocalUrgency(report));
        dto.setSummary(buildSummary(report));

        return dto;
    }

    private String resolveLocalSpecialty(String report, List<Specialty> specialties) {
        String n = normalize(report);

        // Eu começo pelos sintomas mais classicos de risco para aproximar o fallback
        // do raciocinio clinico esperado pelo usuario.
        if (n.contains("peito") || n.contains("cora")) {
            return find("Cardiologia", specialties);
        }
        if (n.contains("cabe") || n.contains("neurol")) {
            return find("Neurologia", specialties);
        }
        if (n.contains("osso") || n.contains("fratur")) {
            return find("Ortopedia", specialties);
        }

        return find(DEFAULT_SPECIALTY_NAME, specialties);
    }

    private String resolveLocalUrgency(String report) {
        String n = normalize(report);

        // Eu marco como alta prioridade quando o texto aponta sinais claros de gravidade,
        // porque a experiencia do paciente nao pode subestimar sintomas de risco.
        if (n.contains("desma") || n.contains("falta de ar") || n.contains("dor no peito")) {
            return "Alta";
        }
        if (n.contains("febre") || n.contains("dor") || n.contains("tontura")) {
            return "Média";
        }
        return "Baixa";
    }

    private String buildSummary(String report) {
        // Eu resumo o texto original sem exagerar para manter a resposta legivel
        // e ao mesmo tempo preservar o contexto do relato.
        String clean = report.strip().replaceAll("\\s+", " ");
        return clean.length() > 180 ? clean.substring(0, 180) + "..." : clean;
    }

    private Specialty resolveSpecialty(String name, List<Specialty> specialties) {
        // Eu amarro o nome devolvido pela IA a uma especialidade realmente cadastrada,
        // porque o frontend precisa trabalhar com dados consistentes do banco.
        String n = normalize(name);

        return specialties.stream()
                .filter(s -> normalize(s.getName()).equals(n))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Especialidade não encontrada"));
    }

    private String find(String target, List<Specialty> specialties) {
        return specialties.stream()
                .filter(s -> normalize(s.getName()).equals(normalize(target)))
                .map(Specialty::getName)
                .findFirst()
                .orElse(DEFAULT_SPECIALTY_NAME);
    }

    private String normalizeUrgency(String urgency) {
        String n = normalize(urgency);

        if (n.contains("alta"))
            return "Alta";
        if (n.contains("media"))
            return "Média";
        return "Baixa";
    }

    private AttendanceRuleSummaryDTO applyUrgencyToWaitTime(AttendanceRuleSummaryDTO rule, String urgency) {
        // Eu sobreponho a urgencia sobre o tempo base da especialidade porque
        // o tempo exibido ao paciente precisa refletir prioridade clinica, nao apenas fila cadastrada.
        Integer baseWaitTime = rule.getAverageWaitTime();
        if (baseWaitTime == null) {
            return rule;
        }

        int adjustedWaitTime = switch (urgency) {
            // Aqui eu capei os casos graves para nao passar a impressao de espera longa
            // quando a triagem ja indicou que o atendimento deve ser prioritario.
            case "Alta" -> Math.min(Math.max(baseWaitTime, 0), 15);
            // Aqui eu mantenho a faixa intermediaria porque ela precisa evitar extremos,
            // mas ainda respeitar um valor util para orientacao do usuario.
            case "Média" -> Math.min(Math.max(baseWaitTime, 15), 45);
            // Aqui eu garanto que a baixa prioridade nao pareca atendimento emergencial.
            default -> Math.max(baseWaitTime, 45);
        };

        rule.setAverageWaitTime(adjustedWaitTime);
        return rule;
    }

    private String normalize(String v) {
        if (v == null)
            return "";
        return Normalizer.normalize(v, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
