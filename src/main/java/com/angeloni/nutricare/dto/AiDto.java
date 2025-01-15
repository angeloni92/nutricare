package com.angeloni.nutricare.dto;

import java.io.Serializable;

import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiDto implements Serializable{
	
	private static final long serialVersionUID = -835673006759659771L;
	
	private Long id;
	
	@NotNull
	private AINameEnum name;

	@NotNull
	private AIModelEnum model;
	
	private String aiKey;

}
