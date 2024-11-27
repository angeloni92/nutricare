package com.angeloni.nutricare.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DietRequestDto implements Serializable {

	private static final long serialVersionUID = -9032680088206075695L;
	
	@NotNull
	private AiDto ai;	
	@NotNull
	private ClientDto client;

}
