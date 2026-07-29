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
import com.angeloni.nutricare.exception.NotFoundException;
import com.angeloni.nutricare.entity.DietResultEntity;
import com.angeloni.nutricare.repository.AiRepository;
import com.angeloni.nutricare.repository.AiUserRepository;
import com.angeloni.nutricare.repository.DietResultRepository;

import lombok.extern.slf4j.Slf4j;
import static com.angeloni.nutricare.service.AuditLogService.*;

@Service
@Slf4j
public class DietServiceImpl implements DietService {

	@Autowired
	private AiRepository aiRepository;

	@Autowired
	private DietResultRepository dietResultRepository;

	@Autowired
	private AiUserRepository aiUserRepository;

	@Autowired
	private AiCheckContext dietGenerationContext;

	@Autowired
	private DietGenerationService dietGenerationService;

	@Autowired private UserContextService userContextService;
	@Autowired private AuditLogService auditLog;

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
		Optional<AiUserEntity> aiUser = dietGenerationContext.check(aiName, dietRequestDto.getAi(), user);
		if (aiUser.isEmpty()) {
			saveAiUser(user, ai, dietRequestDto.getAi().getAiKey());
		}
		dietGenerationService.generateDietAsync(dietRequestDto);
		auditLog.log(DIET_GENERATE, OK, dietRequestDto.getAi().getName().getValue()
				+ " / " + dietRequestDto.getAi().getModel());
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
	@Transactional
	public void deleteDiet(Long id) {
		UserEntity user = userContextService.getCurrentUser();
		DietResultEntity diet = dietResultRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new NotFoundException("Piano nutrizionale con id [" + id + "] non trovato"));
		dietResultRepository.delete(diet);
		auditLog.log(DIET_DELETE, OK, "id=" + id);
		log.info("Diet [{}] deleted by user [{}]", id, user.getId());
	}

	private AiUserEntity saveAiUser(UserEntity user, AiEntity ai, String aiKey) {
		log.info(String.format(SAVE_AI_KEY_FORMAT, aiKey, user.getId()));
		return aiUserRepository.saveAndFlush(AiUserEntity.builder().user(user).ai(ai).aiKey(aiKey).build());
	}

}
