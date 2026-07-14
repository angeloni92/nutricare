package com.angeloni.nutricare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.angeloni.nutricare.base.AbstractBaseTestIT;
import com.angeloni.nutricare.repository.AiRepository;
import com.angeloni.nutricare.repository.AiUserRepository;
import com.angeloni.nutricare.repository.ClientRepository;
import com.angeloni.nutricare.repository.UserRepository;

@AutoConfigureMockMvc
public abstract class AbstractControllerIT extends AbstractBaseTestIT {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected AiUserRepository aiUserRepository;

	@Autowired
	protected AiRepository aiRepository;

	@Autowired
	protected ClientRepository clientRepository;

	protected void clearDb() {
		aiUserRepository.deleteAll();
		userRepository.deleteAll();
		aiRepository.deleteAll();
		clientRepository.deleteAll();
	}

}
