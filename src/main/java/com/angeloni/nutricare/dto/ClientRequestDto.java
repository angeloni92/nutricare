package com.angeloni.nutricare.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientRequestDto {
	
	@NotNull
	private ClientDto client;
	@NotNull
	private AnthropometryDto anthropometry;
	@NotNull
	private DietDetailDto dietDetail;
	

}
