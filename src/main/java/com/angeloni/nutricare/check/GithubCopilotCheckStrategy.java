package com.angeloni.nutricare.check;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.exception.AiKeyException;
import com.angeloni.nutricare.repository.AiUserRepository;

@Component
public class GithubCopilotCheckStrategy implements AiCheckStrategy {

	private static final String AI_KEY_NOT_NULL = "ai key cannot be null";

	@Autowired
	private AiUserRepository aiUserRepository;

	@Override
	public Optional<AiUserEntity> check(AiDto aiDto, UserEntity user) {
		if (Objects.isNull(aiDto.getAiKey())) {
			throw new AiKeyException(AI_KEY_NOT_NULL);
		}
		return aiUserRepository.findByAiKey(aiDto.getAiKey());
	}
}

