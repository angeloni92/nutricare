package com.angeloni.nutricare.service;

import java.util.List;

import com.angeloni.nutricare.dto.DietRequestDto;

public interface DietGeneratorService {

	List<String> getClientsForSelection();

	String generateDiet(DietRequestDto request);

}