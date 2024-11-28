package com.angeloni.nutricare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.check.AiCheckContext;
import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.AINameEnum;
import com.angeloni.nutricare.exception.NotFoundException;
import com.angeloni.nutricare.message.DietStartMessage;
import com.angeloni.nutricare.producer.DietProducer;
import com.angeloni.nutricare.repository.AiRepository;
import com.angeloni.nutricare.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DietServiceImpl implements DietService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AiRepository aiRepository;
	
	@Autowired
	private AiCheckContext dietGenerationContext;
	
	@Autowired
	private DietProducer dietProducer;

	/**
	 * Initiates the process of generating a diet using the specified AI model.
	 * <p>
	 * This method retrieves the user and AI information, validates the AI model, and sends a message
	 * to start the diet generation process. It also handles logging and exception handling.
	 * 
	 * @param dietRequestDto the data transfer object containing the diet request details, including
	 *                       the AI model and related information.
	 * 
	 * @return a message indicating that the diet generation process has been started:
	 *         {@link DietService#START_GENERATE_DIET_MSG}.
	 * 
	 * @throws NotFoundException if the user or AI model is not found in the repository.
	 * 
	 * @see DietRequestDto
	 * @see UserEntity
	 * @see AiEntity
	 * @see DietGenerationContext
	 * @see DietStartMessage
	 * @see DietProducer
	 * @see DietService
	 */
	@Override
	@Transactional
	public String generateDiet(DietRequestDto dietRequestDto) {
		log.info("START generate diet using AI: [%s], model: [%s].", dietRequestDto.getAi().getName().getValue(),
				dietRequestDto.getAi().getModel());
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException(String.format(DietService.USER_NOT_FOUND_FORMAT, username)));
		AiEntity ai = aiRepository
				.findByNameAndModel(dietRequestDto.getAi().getName(), dietRequestDto.getAi().getModel())
				.orElseThrow(() -> new NotFoundException(String.format(DietService.AI_NOT_FOUND_FORMAT,
						dietRequestDto.getAi().getName().getValue(), dietRequestDto.getAi().getModel())));
		String aiName = AINameEnum.CHATGPT.getValue();	
	    dietGenerationContext.check(aiName, dietRequestDto, user, ai);
	    DietStartMessage dietStartMessage = prepareDietStartMessage(user.getId(), dietRequestDto);
	    dietProducer.sendStartDietMessage(dietStartMessage);
		return DietService.START_GENERATE_DIET_MSG;
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

}
