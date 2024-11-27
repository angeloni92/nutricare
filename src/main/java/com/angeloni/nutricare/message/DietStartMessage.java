package com.angeloni.nutricare.message;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.dto.ClientDto;

import lombok.Data;

@Data
public class DietStartMessage {
	
	private Long userId;
	private AiDto ai;
	private ClientDto client;
}
