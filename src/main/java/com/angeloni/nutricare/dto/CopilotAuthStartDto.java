package com.angeloni.nutricare.dto;

import lombok.Data;

@Data
public class CopilotAuthStartDto {

	private String authorizationUrl;
	private String state;
}

