package com.angeloni.nutricare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.AuditLogEntity;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findTop300ByOrderByOccurredAtDesc();
}