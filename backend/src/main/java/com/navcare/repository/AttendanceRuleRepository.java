package com.navcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.navcare.entity.AttendanceRule;

public interface AttendanceRuleRepository extends JpaRepository<AttendanceRule, Long> {

    List<AttendanceRule> findBySpecialty_Id(Long specialtyId);
}
