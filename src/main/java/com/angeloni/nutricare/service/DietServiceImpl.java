package com.angeloni.nutricare.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.check.AiCheckContext;
import com.angeloni.nutricare.dto.CommonResponseDto;
import com.angeloni.nutricare.dto.DietDetailDto;
import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.AINameEnum;
import com.angeloni.nutricare.exception.NotFoundException;
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
	private DietGenerationService dietGenerationService;

	@Autowired
	private UserContextService userContextService;

	@Autowired
	private CopilotAuthService copilotAuthService;

	@Override
	@Transactional
	public CommonResponseDto generateDiet(DietRequestDto dietRequestDto) {
		log.info("START generate diet using AI: [{}], model: [{}].",
				dietRequestDto.getAi().getName().getValue(), dietRequestDto.getAi().getModel());
		UserEntity user = userContextService.getCurrentUser();
		AiEntity ai = aiRepository
				.findByNameAndModel(dietRequestDto.getAi().getName(), dietRequestDto.getAi().getModel())
				.orElseThrow(() -> new NotFoundException(String.format(DietService.AI_NOT_FOUND_FORMAT,
						dietRequestDto.getAi().getName().getValue(), dietRequestDto.getAi().getModel())));
		String aiName = dietRequestDto.getAi().getName().getValue();
		if (AINameEnum.GITHUB_COPILOT.getValue().equals(aiName) && dietRequestDto.getAi().getAiKey() == null) {
			dietRequestDto.getAi().setAiKey(copilotAuthService.resolveAccessTokenForUser(user));
		}
		Optional<AiUserEntity> aiUser = dietGenerationContext.check(aiName, dietRequestDto.getAi(), user);
		if (aiUser.isEmpty() && !AINameEnum.GITHUB_COPILOT.getValue().equals(aiName)) {
			saveAiUser(user, ai, dietRequestDto.getAi().getAiKey());
		}
		dietGenerationService.generateDietAsync(dietRequestDto);
		CommonResponseDto commonResponseDto = new CommonResponseDto();
		commonResponseDto.setStatus(DietService.SUCCESS_STATUS);
		commonResponseDto.setMessage(DietService.START_GENERATE_DIET_MSG);
		return commonResponseDto;
	}

	@Override
	public List<DietDetailDto> getAllDiets() {
		return Collections.emptyList();
	}

	@Override
	public void deleteDiet(Long id) {
		// TODO: implement via DietResultRepository
	}

	private AiUserEntity saveAiUser(UserEntity user, AiEntity ai, String aiKey) {
		log.info(String.format(SAVE_AI_KEY_FORMAT, aiKey, user.getId()));
		return aiUserRepository.saveAndFlush(AiUserEntity.builder().user(user).ai(ai).aiKey(aiKey).build());
	}

}