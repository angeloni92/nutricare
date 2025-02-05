package com.angeloni.nutricare.dto;

import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiDto {
	
	private Long id;
	
	@NotNull
	private AINameEnum name;

	@NotNull
	private AIModelEnum model;
	
	private String aiKey;

}
