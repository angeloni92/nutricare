package com.angeloni.nutricare.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angeloni.nutricare.dto.AiUserDto;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.repository.AiUserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiUserServiceImpl implements AiUserService {

	@Autowired
	private AiUserRepository aiUserRepository;

	@Autowired
	private UserContextService userContextService;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public AiUserDto getByUser() {
		UserEntity user = userContextService.getCurrentUser();
		return aiUserRepository.findByUser(user).map(x -> modelMapper.map(x, AiUserDto.class)).orElse(new AiUserDto());
	}

}