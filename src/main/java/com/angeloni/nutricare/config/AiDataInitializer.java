package com.angeloni.nutricare.config;

import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;
import com.angeloni.nutricare.repository.AiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class AiDataInitializer {

    @Autowired
    private AiRepository aiRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAiModels() {
        List<AiEntity> existing = aiRepository.findAll();
        long maxId = existing.stream().mapToLong(AiEntity::getId).max().orElse(0L);
        AtomicLong nextId = new AtomicLong(maxId + 1);

        for (AIModelEnum model : AIModelEnum.values()) {
            AINameEnum provider = resolveProvider(model);
            if (aiRepository.findByNameAndModel(provider, model).isEmpty()) {
                AiEntity entity = AiEntity.builder()
                        .id(nextId.getAndIncrement())
                        .name(provider)
                        .model(model)
                        .build();
                aiRepository.save(entity);
                log.debug("Registered AI model: {}/{}", provider, model);
            }
        }
        log.info("AI model table initialized ({} total)", aiRepository.count());
    }

    private AINameEnum resolveProvider(AIModelEnum model) {
        String name = model.name();
        if (name.startsWith("CLAUDE"))  return AINameEnum.CLAUDE;
        if (name.startsWith("GEMINI_")) return AINameEnum.GEMINI;
        return AINameEnum.CHATGPT;
    }
}
