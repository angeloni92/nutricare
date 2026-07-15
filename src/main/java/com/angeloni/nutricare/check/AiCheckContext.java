package com.angeloni.nutricare.check;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.AINameEnum;

import jakarta.annotation.PostConstruct;

@Component
public class AiCheckContext {

    public static final String CHATGPT = AINameEnum.CHATGPT.getValue();
    public static final String CLAUDE  = AINameEnum.CLAUDE.getValue();

    private static final String AI_NOT_FOUND_FORMAT = "Strategy not found for AI: [%S]";

    @Autowired
    private ChatGptCheckStrategy chatGptDietGenerationStrategy;

    @Autowired
    private ClaudeCheckStrategy claudeCheckStrategy;

    private final Map<String, AiCheckStrategy> strategies = new HashMap<>();

    @PostConstruct
    public void initStrategies() {
        strategies.put(CHATGPT, chatGptDietGenerationStrategy);
        strategies.put(CLAUDE,  claudeCheckStrategy);
    }

    public Optional<AiUserEntity> check(String aiName, AiDto aiDto, UserEntity user) {
        AiCheckStrategy strategy = strategies.get(aiName);
        if (strategy != null) {
            return strategy.check(aiDto, user);
        } else {
            throw new IllegalArgumentException(String.format(AI_NOT_FOUND_FORMAT, aiName));
        }
    }
}
