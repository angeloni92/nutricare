package com.angeloni.nutricare.service;

import com.angeloni.nutricare.dto.AiUserDto;
import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;

public interface AiUserService {

	AiUserDto getByUser();

	boolean hasApiKey(AINameEnum name, AIModelEnum model);

	void saveApiKey(AINameEnum name, AIModelEnum model, String plainApiKey);

}
