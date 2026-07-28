package com.angeloni.nutricare.service;

import com.angeloni.nutricare.entity.UserEntity;

public interface AuthService {

    /** Login. Lancia AuthException se credenziali errate. */
    UserEntity login(String username, String password);

    /** Registra nuovo utente. Lancia IllegalArgumentException se username/email gia esistente. */
    UserEntity register(String username, String password, String email);

    /** True se non esistono utenti reali nel DB (primo avvio). */
    boolean isFirstRun();
}
