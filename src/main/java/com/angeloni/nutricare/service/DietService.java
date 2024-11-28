package com.angeloni.nutricare.service;

import com.angeloni.nutricare.dto.DietRequestDto;

public interface DietService {

	public static final String AI_NOT_FOUND_FORMAT = "AI name: [%s], model : [%s] not found";
	public static final String USER_NOT_FOUND_FORMAT = "User username: [%s] not found";
	public static final String START_GENERATE_DIET_MSG = "The diet generation process has been successfully started. You can check the status of the diet generation in your personal area, or wait for the notification that will inform you upon completion of the process.";

	String generateDiet(DietRequestDto dietRequest);
}
