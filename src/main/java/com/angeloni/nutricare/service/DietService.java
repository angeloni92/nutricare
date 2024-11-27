package com.angeloni.nutricare.service;

import com.angeloni.nutricare.dto.DietRequestDto;
public interface DietService {
	
    String generateDiet(DietRequestDto dietRequest);
}
