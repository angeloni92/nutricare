package com.angeloni.nutricare.check;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.UserEntity;

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
     * Checks the diet request based on the specified AI name using the appropriate strategy.
     * <p>
     * This method retrieves the strategy for the given AI name from a map of available strategies and invokes
     * the corresponding check method. If no strategy is found for the provided AI name, an {@link IllegalArgumentException}
     * is thrown.
     * 
     * @param aiName the name of the AI for which the check is to be performed. Must not be {@code null} or empty.
     * @param dietRequestDto the data transfer object containing the diet request details. Must not be {@code null}.
     * @param user the user associated with the diet request. Must not be {@code null}.
     * @param ai the AI entity associated with the specified AI name. Must not be {@code null}.
     * 
     * @throws IllegalArgumentException if no strategy is found for the provided AI name.
     * 
     * @see AiCheckStrategy
     * @see DietRequestDto
     * @see UserEntity
     * @see AiEntity
     */
    public void check(String aiName, DietRequestDto dietRequestDto, UserEntity user, AiEntity ai) {
        AiCheckStrategy strategy = strategies.get(aiName);
        if (strategy != null) {
            strategy.check(dietRequestDto, user, ai);
        } else {
            throw new IllegalArgumentException(String.format(AI_NOT_FOUND_FORMAT , aiName));
        }
    }

}
