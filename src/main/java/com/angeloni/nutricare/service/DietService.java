package com.angeloni.nutricare.service;

import java.util.List;

import com.angeloni.nutricare.dto.CommonResponseDto;
import com.angeloni.nutricare.dto.DietDetailDto;
import com.angeloni.nutricare.dto.DietRequestDto;

public interface DietService {

	public static final String AI_NOT_FOUND_FORMAT = "AI name: [%s], model : [%s] not found";
	public static final String USER_NOT_FOUND_FORMAT = "User username: [%s] not found";
	public static final String START_GENERATE_DIET_MSG = "The diet generation process has been successfully started. You can check the status of the diet generation in your personal area, or wait for the notification that will inform you upon completion of the process.";
	public static final String SAVE_AI_KEY_FORMAT = "START saving ai key : [%S] for user id : [%s]";
	public static final String SUCCESS_STATUS = "Success";
	
	CommonResponseDto generateDiet(DietRequestDto dietRequest);

	List<DietDetailDto> getAllDiets();

	void deleteDiet(Long id);
}
