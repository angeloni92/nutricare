package com.angeloni.nutricare.check;

import java.util.Optional;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;

public interface AiCheckStrategy {
	
	Optional<AiUserEntity> check(AiDto aiDto, UserEntity user);
}
