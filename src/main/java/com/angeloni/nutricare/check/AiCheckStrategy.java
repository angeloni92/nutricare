package com.angeloni.nutricare.check;

import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.UserEntity;

public interface AiCheckStrategy {
	
	void check(DietRequestDto dietRequestDto, UserEntity user, AiEntity ai);
}
