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
	private static final String DESKTOP_EMAIL = "desktop@nutricare.local";

	@Autowired
	private UserRepository userRepository;

	private UserEntity currentUser;

	@PostConstruct
	@Transactional
	public void init() {
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
		log.info("Desktop user loaded: {}", currentUser.getUsername());
	}

	@Override
	public UserEntity getCurrentUser() {
		return currentUser;
	}

}