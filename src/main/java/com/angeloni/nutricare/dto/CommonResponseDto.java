package com.angeloni.nutricare.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommonResponseDto implements Serializable {
	
	private static final long serialVersionUID = 5009304229753132979L;

	@NotBlank
	private String status;
	
	@NotBlank
	private String message;
	

}
