package com.navcare.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.navcare.dto.AttendanceRuleRequestDTO;
import com.navcare.dto.AttendanceRuleResponseDTO;
import com.navcare.entity.AttendanceRule;
import com.navcare.entity.Specialty;
import com.navcare.exception.ResourceNotFoundException;
import com.navcare.mapper.AttendanceRuleMapper;
import com.navcare.repository.AttendanceRuleRepository;
import com.navcare.repository.SpecialtyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceRuleService {

    // Aqui eu concentro o CRUD de regras para manter o controller fino e
    // deixar a relacao com especialidades bem controlada.
    private final AttendanceRuleRepository attendanceRuleRepository;
    private final SpecialtyRepository specialtyRepository;

    @Transactional(readOnly = true)
    public List<AttendanceRuleResponseDTO> findAll() {
        // Eu ordeno por id para deixar a listagem previsivel na area administrativa.
        return attendanceRuleRepository.findAll().stream()
            .sorted(Comparator.comparing(AttendanceRule::getId))
            .map(AttendanceRuleMapper::toResponseDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public AttendanceRuleResponseDTO findById(Long id) {
        return AttendanceRuleMapper.toResponseDTO(getEntityById(id));
    }

    @Transactional
    public AttendanceRuleResponseDTO create(AttendanceRuleRequestDTO dto) {
        // Eu valido a especialidade antes de gravar porque a regra so faz sentido
        // se estiver vinculada a um cadastro real ja existente.
        Specialty specialty = getSpecialtyById(dto.getSpecialtyId());
        AttendanceRule attendanceRule = AttendanceRuleMapper.toEntity(dto, specialty);
        return AttendanceRuleMapper.toResponseDTO(attendanceRuleRepository.save(attendanceRule));
    }

    @Transactional
    public AttendanceRuleResponseDTO update(Long id, AttendanceRuleRequestDTO dto) {
        // Eu atualizo a entidade carregada para preservar o relacionamento com a especialidade.
        AttendanceRule attendanceRule = getEntityById(id);
        Specialty specialty = getSpecialtyById(dto.getSpecialtyId());
        AttendanceRuleMapper.updateEntity(attendanceRule, dto, specialty);
        return AttendanceRuleMapper.toResponseDTO(attendanceRuleRepository.save(attendanceRule));
    }

    @Transactional
    public void delete(Long id) {
        AttendanceRule attendanceRule = getEntityById(id);
        attendanceRuleRepository.delete(attendanceRule);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRule> findBySpecialtyId(Long specialtyId) {
        // Eu exponho esta consulta para a triagem reaproveitar as regras sem duplicar logica de acesso.
        return attendanceRuleRepository.findBySpecialty_Id(specialtyId);
    }

    @Transactional(readOnly = true)
    public AttendanceRule getEntityById(Long id) {
        // Eu centralizo a busca para manter a mensagem de erro consistente em todo o CRUD.
        return attendanceRuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Regra de atendimento não encontrada para o id " + id + "."));
    }

    @Transactional(readOnly = true)
    public Specialty getSpecialtyById(Long id) {
        // Eu valido a existencia da especialidade antes de criar o vínculo da regra.
        return specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Especialidade não encontrada para o id " + id + "."));
    }
}
