package com.navcare.service;

import java.util.List;
import java.util.Comparator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.navcare.dto.SpecialtyRequestDTO;
import com.navcare.dto.SpecialtyResponseDTO;
import com.navcare.entity.Specialty;
import com.navcare.exception.ResourceNotFoundException;
import com.navcare.mapper.SpecialtyMapper;
import com.navcare.repository.SpecialtyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    // Aqui eu concentro o CRUD de especialidades para evitar que o controller
    // precise conhecer detalhes de ordenacao, conversao e tratamento de erro.
    private final SpecialtyRepository specialtyRepository;

    @Transactional(readOnly = true)
    public List<SpecialtyResponseDTO> findAll() {
        // Eu ordeno por nome para manter a lista previsivel na tela administrativa
        // e no prompt usado pela triagem.
        return specialtyRepository.findAll().stream()
            .sorted(Comparator.comparing(Specialty::getName))
            .map(SpecialtyMapper::toResponseDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public SpecialtyResponseDTO findById(Long id) {
        Specialty specialty = getEntityById(id);
        return SpecialtyMapper.toResponseDTO(specialty);
    }

    @Transactional
    public SpecialtyResponseDTO create(SpecialtyRequestDTO dto) {
        // Eu converto DTO em entidade aqui para manter o controller fino e o contrato limpo.
        Specialty specialty = SpecialtyMapper.toEntity(dto);
        Specialty saved = specialtyRepository.save(specialty);
        return SpecialtyMapper.toResponseDTO(saved);
    }

    @Transactional
    public SpecialtyResponseDTO update(Long id, SpecialtyRequestDTO dto) {
        // Eu reaproveito a entidade carregada para preservar o ciclo de vida do JPA.
        Specialty specialty = getEntityById(id);
        SpecialtyMapper.updateEntity(specialty, dto);
        return SpecialtyMapper.toResponseDTO(specialtyRepository.save(specialty));
    }

    @Transactional
    public void delete(Long id) {
        Specialty specialty = getEntityById(id);
        specialtyRepository.delete(specialty);
    }

    @Transactional(readOnly = true)
    public Specialty getEntityById(Long id) {
        // Eu centralizo a busca aqui para padronizar a mensagem de erro de nao encontrado.
        return specialtyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Especialidade não encontrada para o id " + id + "."));
    }
}
