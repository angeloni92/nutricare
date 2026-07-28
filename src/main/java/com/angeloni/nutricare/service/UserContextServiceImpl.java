package com.angeloni.nutricare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserContextServiceImpl implements UserContextService {

	private static final String DESKTOP_USERNAME = "desktop-user";
	private static final String DESKTOP_EMAIL    = "desktop@nutricare.local";

	@Autowired
	private UserRepository userRepository;

	private UserEntity currentUser;

	@PostConstruct
	@Transactional
	public void init() {
		// Se esistono utenti con password reale, è un'installazione multi-utente:
		// il login verrà gestito dal LoginDialog, non facciamo auto-login.
		boolean hasRealUsers = userRepository.findAll().stream()
				.anyMatch(u -> !DESKTOP_USERNAME.equals(u.getUsername())
						&& u.getPassword() != null
						&& !u.getPassword().isEmpty());
		if (hasRealUsers) {
			log.info("Multi-user mode: login required at startup");
			return;
		}
		// Compatibilità: installazione single-user senza autenticazione
		currentUser = userRepository.findByUsername(DESKTOP_USERNAME).orElseGet(() -> {
			log.info("Creating default desktop user");
			UserEntity user = UserEntity.builder()
					.username(DESKTOP_USERNAME)
					.password("")
					.email(DESKTOP_EMAIL)
					.emailConfirmed(Boolean.TRUE)
					.build();
			return userRepository.save(user);
		});
		log.info("Single-user mode: auto-logged as {}", currentUser.getUsername());
	}

	@Override
	public UserEntity getCurrentUser() {
		return currentUser;
	}

	@Override
	public void setCurrentUser(UserEntity user) {
		this.currentUser = user;
		log.info("Current user set to: {}", user != null ? user.getUsername() : "null");
	}

	@Override
	public void logout() {
		log.info("User logged out: {}", currentUser != null ? currentUser.getUsername() : "none");
		this.currentUser = null;
	}

}
