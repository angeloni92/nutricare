package com.angeloni.nutricare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommonResponseDto {

	@NotBlank
	private String status;
	
	@NotBlank
	private String message;
	

}
