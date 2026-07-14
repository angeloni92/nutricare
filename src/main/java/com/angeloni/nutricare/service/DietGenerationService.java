package com.angeloni.nutricare.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.ai.AIHandlerFactory;
import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.entity.DietResultEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.repository.DietResultRepository;
import com.angeloni.nutricare.util.DataProcessor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DietGenerationService {

	@Autowired
	private AIHandlerFactory aiHandlerFactory;

	@Autowired
	private PromptService promptService;

	@Autowired
	private DataProcessor dataProcessor;

	@Autowired
	private DietResultRepository dietResultRepository;

	@Autowired
	private AuthService authService;

	@Transactional
	public CompletableFuture<String> generateDietAsync(DietRequestDto payload) {
		log.info("START generating diet asynchronously");
		UserEntity user = authService.retrieveUserFromAuthentication();

		return CompletableFuture.supplyAsync(() -> {
			try {
				// Flatten request data
				Map<String, String> flattenedData = dataProcessor.flattenObject(payload);

				// Get prompt template
				String promptTemplate = promptService.getDietPromptTemplate();

				// Replace variables in template
				String finalPrompt = promptService.replacePromptVariables(promptTemplate, flattenedData);

				// Call AI handler factory with the model specified in request
				String aiModel = payload.getAi().getModel().getValue();
				String dietResponse = aiHandlerFactory.generateDietResponse(aiModel, finalPrompt).join();

				// Save result to database
				DietResultEntity result = DietResultEntity.builder()
						.user(user)
						.clientId(payload.getClientRequest().getClient().getId())
						.generatedDiet(dietResponse)
						.aiModel(aiModel)
						.createdAt(LocalDateTime.now())
						.build();

				dietResultRepository.save(result);
				log.info("STOP generating diet - saved to DB");
				return dietResponse;

			} catch (Exception e) {
				log.error("Error generating diet: {}", e.getMessage(), e);
				throw new RuntimeException("Diet generation failed", e);
			}
		});
	}

	public DietGenerationService() {
	}
}

