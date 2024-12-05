package com.angeloni.nutricare.check;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.exception.AiKeyException;
import com.angeloni.nutricare.repository.AiUserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class AiCheckContext {
	
	public static final String CHATGPT = "ChatGPT";
	private static final String AI_NOT_FOUND_FORMAT = "Strategy not found for AI: [%S]";
	
	@Autowired
	private ChatGptCheckStrategy chatGptDietGenerationStrategy;
	
	private final Map<String, AiCheckStrategy> strategies = new HashMap<>();

    @PostConstruct
    public void initStrategies() {
        strategies.put(CHATGPT, chatGptDietGenerationStrategy);
    }

    /**
     * Validates the AI key in the provided diet request and ensures it is associated with the specified user.
     * <p>
     * This method checks whether the AI key provided in the {@link AiDto} is {@code null}. If the AI key is 
     * {@code null}, it throws an {@link AiKeyException}. If the AI key is valid, the method attempts to find an 
     * existing {@link AiUserEntity} using the provided key. If no associated entity is found, the caller is 
     * responsible for handling the creation of a new association, typically by calling 
     * {@link #saveAiUser(UserEntity, AiEntity, String)}.
     * </p>
     * 
     * @param aiDto the data transfer object containing AI details, including the AI key; must not be {@code null}.
     * @param user the user to whom the AI key should be associated; must not be {@code null}.
     * @return an {@link Optional} containing the associated {@link AiUserEntity}, if found; otherwise, an empty {@link Optional}.
     * 
     * @throws AiKeyException if the AI key in the {@link AiDto} is {@code null} or invalid.
     * 
     * @see AiDto
     * @see UserEntity
     * @see AiEntity
     * @see AiUserEntity
     * @see AiUserRepository
     * @see AiKeyException
     */
    public Optional<AiUserEntity> check(String aiName, AiDto aiDto, UserEntity user) {
        AiCheckStrategy strategy = strategies.get(aiName);
        if (strategy != null) {
           return strategy.check(aiDto, user);
        } else {
            throw new IllegalArgumentException(String.format(AI_NOT_FOUND_FORMAT , aiName));
        }
    }

}
