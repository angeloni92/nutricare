package com.angeloni.nutricare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;

import com.angeloni.nutricare.base.AbstractBaseTestIT;
import com.angeloni.nutricare.repository.AiRepository;
import com.angeloni.nutricare.repository.AiUserRepository;
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
	
	@MockBean
	JmsTemplate jmsTemplate;

	protected void clearDb() {
		aiUserRepository.deleteAll();
		userRepository.deleteAll();
		aiRepository.deleteAll();
	}

}
