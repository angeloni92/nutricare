package com.angeloni.nutricare.check;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.exception.AiKeyException;
import com.angeloni.nutricare.repository.AiUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatGptCheckStrategy implements AiCheckStrategy {

	private static final String AI_KEY_NOT_NULL = "ai key cannot be null";

	@Autowired
	private AiUserRepository aiUserRepository;

	/**
	 * Checks the validity of the AI key in the provided diet request and ensures it is associated with the user.
	 * <p>
	 * This method verifies if the AI key in the {@link DietRequestDto} is not {@code null}. If it is {@code null},
	 * an {@link AiKeyException} is thrown. If the key is valid, the method attempts to find an existing {@link AiUserEntity}
	 * by the given AI key. If no such entity is found, it calls {@link #saveAiUser(UserEntity, AiEntity, String)} 
	 * to create and save a new association.
	 * 
	 * @param aiDto the data transfer object containing the diet request details, including the AI key. Must not be {@code null}.
	 * @param user the user associated with the AI key. Must not be {@code null}.
	 * @throws AiKeyException if the AI key is {@code null} or invalid.
	 * 
	 * @see DietRequestDto
	 * @see UserEntity
	 * @see AiEntity
	 * @see AiUserEntity
	 * @see AiUserRepository
	 * @see AiKeyException
	 */
	@Override
	public Optional<AiUserEntity> check(AiDto aiDto, UserEntity user) {
		if (Objects.isNull(aiDto.getAiKey())) {
			throw new AiKeyException(AI_KEY_NOT_NULL);
		}
		return aiUserRepository.findByAiKey(aiDto.getAiKey());
	}
}
