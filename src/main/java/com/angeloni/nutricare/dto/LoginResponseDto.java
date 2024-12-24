package com.angeloni.nutricare.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginResponseDto implements Serializable {
	
	private static final long serialVersionUID = -3026876230682369832L;
	
	@NotBlank
	private String status;
	
	@NotBlank
	private String token;	

}
