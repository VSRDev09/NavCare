package com.navcare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.navcare.entity.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    Optional<Specialty> findByNameIgnoreCase(String name);
}
