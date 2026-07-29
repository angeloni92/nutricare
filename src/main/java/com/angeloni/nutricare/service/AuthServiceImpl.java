package com.angeloni.nutricare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.UserRoleEnum;
import com.angeloni.nutricare.exception.AuthException;
import com.angeloni.nutricare.repository.UserRepository;
import com.angeloni.nutricare.util.PasswordUtil;

import lombok.extern.slf4j.Slf4j;
import static com.angeloni.nutricare.service.AuditLogService.*;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String DESKTOP_USERNAME = "desktop-user";

    @Autowired private UserRepository userRepository;
    @Autowired private UserContextService userContextService;
    @Autowired private AuditLogService auditLog;

    @Override
    @Transactional
    public UserEntity login(String username, String password) {
        String trimmed = username.trim();
        try {
            UserEntity user = userRepository.findByUsername(trimmed)
                    .orElseThrow(() -> new AuthException("Credenziali non valide"));
            if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
                throw new AuthException("Credenziali non valide");
            }
            userContextService.setCurrentUser(user);
            auditLog.logAs(user.getId(), user.getUsername(), LOGIN_OK, OK, null);
            log.info("User logged in: {}", user.getUsername());
            return user;
        } catch (AuthException e) {
            auditLog.logAs(null, trimmed, LOGIN_FAIL, FAIL, null);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserEntity register(String username, String password, String email) {
        String trimUser  = username.trim();
        String trimEmail = email.trim().toLowerCase();
        if (userRepository.existsByUsername(trimUser)) {
            throw new IllegalArgumentException("Username gia in uso: " + trimUser);
        }
        if (userRepository.existsByEmail(trimEmail)) {
            throw new IllegalArgumentException("Email gia registrata: " + trimEmail);
        }
        UserEntity user = UserEntity.builder()
                .username(trimUser)
                .password(PasswordUtil.hashPassword(password))
                .email(trimEmail)
                .role(UserRoleEnum.USER)
                .emailConfirmed(Boolean.TRUE)
                .build();
        user = userRepository.save(user);
        userContextService.setCurrentUser(user);
        auditLog.logAs(user.getId(), user.getUsername(), REGISTER, OK, null);
        log.info("New user registered: {}", user.getUsername());
        return user;
    }

    @Override
    public boolean isFirstRun() {
        return userRepository.findAll().stream()
                .noneMatch(u -> !DESKTOP_USERNAME.equals(u.getUsername())
                             && u.getPassword() != null
                             && !u.getPassword().isEmpty());
    }
}
