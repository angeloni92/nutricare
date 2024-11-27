package com.angeloni.nutricare.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiDto implements Serializable{
	
	private static final long serialVersionUID = -835673006759659771L;
	
	@NotBlank(message = "name is required")
	private String name;

	@NotBlank(message = "model is required")
	private String model;

}
