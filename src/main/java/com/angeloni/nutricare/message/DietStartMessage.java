package com.angeloni.nutricare.message;

import com.angeloni.nutricare.dto.DietRequestDto;

import lombok.Data;

@Data
public class DietStartMessage {
	
	private Long userId;
	private DietRequestDto dietRequest;
}
