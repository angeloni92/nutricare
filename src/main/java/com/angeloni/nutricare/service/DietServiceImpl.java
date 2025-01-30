package com.angeloni.nutricare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.check.AiCheckContext;
import com.angeloni.nutricare.dto.CommonResponseDto;
import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.AINameEnum;
import com.angeloni.nutricare.exception.NotFoundException;
import com.angeloni.nutricare.message.DietStartMessage;
import com.angeloni.nutricare.producer.DietStartProducer;
import com.angeloni.nutricare.repository.AiRepository;
import com.angeloni.nutricare.repository.AiUserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DietServiceImpl implements DietService {

	@Autowired
	private AiRepository aiRepository;
	
	@Autowired
	private AiUserRepository aiUserRepository;
	
	@Autowired
	private AiCheckContext dietGenerationContext;
	
	@Autowired
	private DietStartProducer dietProducer;
	
	@Autowired
	private AuthService authService;

	/**
	 * Generates a personalized diet plan using AI based on the provided request details.
	 * 
	 * @param dietRequestDto {@link DietRequestDto} the details of the diet request, including AI configuration and user input
	 * @return a confirmation message indicating the start of diet generation
	 * 
	 * @throws NotFoundException if the user or specified AI model is not found
	 * 
	 * The method performs the following steps:
	 * 1. Logs the initiation of the diet generation process with AI and model details.
	 * 2. Retrieves the authenticated user's details from the security context.
	 * 3. Verifies the existence of the user in the database; throws an exception if not found.
	 * 4. Verifies the existence of the specified AI entity in the database; throws an exception if not found.
	 * 5. Performs additional checks for AI configuration compatibility.
	 * 6. Prepares and sends a diet generation start message to the messaging system.
	 *
	 */
	@Override
	@Transactional
	public CommonResponseDto generateDiet(DietRequestDto dietRequestDto) {
		log.info("START generate diet using AI: [%s], model: [%s].", dietRequestDto.getAi().getName().getValue(),
				dietRequestDto.getAi().getModel());
		UserEntity user = authService.retrieveUserFromAuthentication();;
		AiEntity ai = aiRepository
				.findByNameAndModel(dietRequestDto.getAi().getName(), dietRequestDto.getAi().getModel())
				.orElseThrow(() -> new NotFoundException(String.format(DietService.AI_NOT_FOUND_FORMAT,
						dietRequestDto.getAi().getName().getValue(), dietRequestDto.getAi().getModel())));
		String aiName = AINameEnum.CHATGPT.getValue();	
	    dietGenerationContext.check(aiName, dietRequestDto.getAi(), user).orElse(saveAiUser(user, ai, dietRequestDto.getAi().getAiKey()));
	    DietStartMessage dietStartMessage = prepareDietStartMessage(user.getId(), dietRequestDto);
	    dietProducer.sendStartDietMessage(dietStartMessage);
	    CommonResponseDto commonResponseDto = new CommonResponseDto();
	    commonResponseDto.setStatus(DietService.SUCCESS_STATUS);
	    commonResponseDto.setMessage(DietService.START_GENERATE_DIET_MSG);
		return commonResponseDto;
	}
	
	/**
	 * Prepares a {@link DietStartMessage} object using the provided user ID and diet request data.
	 * <p>
	 * This method creates a new {@link DietStartMessage} instance, sets the user ID and the diet
	 * request information, and returns the prepared message.
	 * 
	 * @param userId the ID of the user initiating the diet request. Must not be {@code null}.
	 * @param dietRequestDto the data transfer object containing the diet request details. Must not be {@code null}.
	 * 
	 * @return a {@link DietStartMessage} containing the user ID and diet request details.
	 * 
	 * @throws IllegalArgumentException if either the {@code userId} or {@code dietRequestDto} is {@code null}.
	 * 
	 * @see DietStartMessage
	 * @see DietRequestDto
	 */
	private DietStartMessage prepareDietStartMessage(Long userId, DietRequestDto dietRequestDto) {
		DietStartMessage dietStartMessage = new DietStartMessage();
		dietStartMessage.setUserId(userId);
		dietStartMessage.setDietRequest(dietRequestDto);
		return dietStartMessage;
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
