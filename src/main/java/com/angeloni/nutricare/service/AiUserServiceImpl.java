package com.angeloni.nutricare.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.dto.AiUserDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;
import com.angeloni.nutricare.repository.AiRepository;
import com.angeloni.nutricare.repository.AiUserRepository;
import com.angeloni.nutricare.util.TokenCryptoUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiUserServiceImpl implements AiUserService {

	@Autowired
	private AiUserRepository aiUserRepository;

	@Autowired
	private AiRepository aiRepository;

	@Autowired
	private UserContextService userContextService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private TokenCryptoUtil tokenCryptoUtil;

	@Override
	public AiUserDto getByUser() {
		UserEntity user = userContextService.getCurrentUser();
		return aiUserRepository.findByUser(user)
				.map(x -> modelMapper.map(x, AiUserDto.class))
				.orElse(new AiUserDto());
	}

	@Override
	public boolean hasApiKey(AINameEnum name, AIModelEnum model) {
		UserEntity user = userContextService.getCurrentUser();
		return aiRepository.findByNameAndModel(name, model)
				.flatMap(ai -> aiUserRepository.findByUserAndAi(user, ai))
				.map(entity -> entity.getAiKey() != null && !entity.getAiKey().isBlank())
				.orElse(false);
	}

	@Override
	@Transactional
	public void saveApiKey(AINameEnum name, AIModelEnum model, String plainApiKey) {
		UserEntity user = userContextService.getCurrentUser();
		AiEntity ai = aiRepository.findByNameAndModel(name, model)
				.orElseThrow(() -> new IllegalArgumentException("AI non trovata: " + name + "/" + model));
		AiUserEntity entity = aiUserRepository.findByUserAndAi(user, ai)
				.orElse(AiUserEntity.builder().user(user).ai(ai).build());
		entity.setAiKey(tokenCryptoUtil.encrypt(plainApiKey));
		aiUserRepository.save(entity);
		log.info("API Key salvata per AI: {}/{}", name, model);
	}
}