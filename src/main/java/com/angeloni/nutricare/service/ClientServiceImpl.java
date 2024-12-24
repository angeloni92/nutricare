package com.angeloni.nutricare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.entity.UserEntity;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
	
	@Autowired
	private UserService userService;
	
	@Override
	public ClientDto createClient(ClientDto clientDto) {
		UserEntity user = userService.getUserFromAuthentication();
		
		return null;
	}

}
