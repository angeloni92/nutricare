package com.angeloni.nutricare.service;

import java.util.List;

import com.angeloni.nutricare.dto.AiDto;

public interface AiService {
	
//	public static final String CLIENT_ALREADY_PRESENT_FORMAT = "Client name: [%s], surname : [%s] is already present";
	
	List<AiDto> getAis();
	
}
