package com.angeloni.nutricare.check;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	private static final String SAVE_AI_KEY_FORMAT = "START saving ai key : [%S] for user id : [%s]";

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
	 * @param dietRequestDto the data transfer object containing the diet request details, including the AI key. Must not be {@code null}.
	 * @param user the user associated with the AI key. Must not be {@code null}.
	 * @param ai the AI entity associated with the AI key. Must not be {@code null}.
	 * 
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
	public void check(DietRequestDto dietRequestDto, UserEntity user, AiEntity ai) {
		if (Objects.isNull(dietRequestDto.getAiKey())) {
			throw new AiKeyException(AI_KEY_NOT_NULL);
		}
		aiUserRepository.findByAiKey(dietRequestDto.getAiKey()).orElse(saveAiUser(user, ai, dietRequestDto.getAiKey()));
	}

	/**
	 * Saves a new {@link AiUserEntity} with the specified user, AI, and AI key.
	 * <p>
	 * This method creates a new {@link AiUserEntity} using the provided user, AI, and AI key, and persists it
	 * in the repository. The entity is saved immediately using {@link AiUserRepository#saveAndFlush(Object)}.
	 * 
	 * @param user the {@link UserEntity} associated with the AI key. Must not be {@code null}.
	 * @param ai the {@link AiEntity} associated with the AI key. Must not be {@code null}.
	 * @param aiKey the AI key to be saved, associated with the user and AI. Must not be {@code null} or empty.
	 * 
	 * @return the saved {@link AiUserEntity} object.
	 * 
	 * @throws IllegalArgumentException if any of the parameters are {@code null} or empty.
	 * 
	 * @see AiUserEntity
	 * @see AiUserRepository
	 * @see UserEntity
	 * @see AiEntity
	 */
	private AiUserEntity saveAiUser(UserEntity user, AiEntity ai, String aiKey) {
		log.info(String.format(SAVE_AI_KEY_FORMAT, aiKey, user.getId()));
		return aiUserRepository.saveAndFlush(AiUserEntity.builder().user(user).ai(ai).aiKey(aiKey).build());
	}
}
