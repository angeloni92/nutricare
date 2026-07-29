package com.angeloni.nutricare.service;

import java.util.List;

import com.angeloni.nutricare.entity.AuditLogEntity;

public interface AuditLogService {

    String LOGIN_OK      = "LOGIN_OK";
    String LOGIN_FAIL    = "LOGIN_FAIL";
    String REGISTER      = "REGISTER";
    String CLIENT_CREATE = "CLIENT_CREATE";
    String CLIENT_UPDATE = "CLIENT_UPDATE";
    String CLIENT_DELETE = "CLIENT_DELETE";
    String DIET_GENERATE = "DIET_GENERATE";
    String DIET_DELETE   = "DIET_DELETE";
    String VISIT_CREATE  = "VISIT_CREATE";
    String BACKUP        = "BACKUP";

    String OK   = "OK";
    String FAIL = "FAIL";

    /** Logs using the user currently in context. */
    void log(String action, String outcome, String details);

    /** Logs with explicit identity (e.g. login attempt before context is set). */
    void logAs(Long userId, String username, String action, String outcome, String details);

    List<AuditLogEntity> findRecent();
}