package com.angeloni.nutricare.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DietRequestDto  {
	
	@NotNull
	private AiDto ai;	
	@NotNull
	private ClientRequestDto clientRequest;
	

}
