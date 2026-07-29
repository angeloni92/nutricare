package com.angeloni.nutricare.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.entity.AuditLogEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.repository.AuditLogRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private UserContextService userContextService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String outcome, String details) {
        UserEntity user = userContextService.getCurrentUser();
        Long userId = null;
        String username = "unknown";
        if (user != null) {
            userId   = user.getId();
            username = user.getUsername();
        }
        persist(userId, username, action, outcome, details);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAs(Long userId, String username, String action, String outcome, String details) {
        persist(userId, username, action, outcome, details);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogEntity> findRecent() {
        try {
            return auditLogRepository.findTop300ByOrderByOccurredAtDesc();
        } catch (Exception e) {
            log.warn("Audit log read failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private void persist(Long userId, String username, String action, String outcome, String details) {
        try {
            auditLogRepository.save(AuditLogEntity.builder()
                    .occurredAt(LocalDateTime.now())
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .outcome(outcome)
                    .details(details)
                    .build());
        } catch (Exception e) {
            log.warn("Audit log write failed [action={}]: {}", action, e.getMessage());
        }
    }
}