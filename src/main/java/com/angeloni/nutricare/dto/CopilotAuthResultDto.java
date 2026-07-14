package com.angeloni.nutricare.dto;

import lombok.Data;

@Data
public class CopilotAuthResultDto {

	private String status;
	private String message;
	private String githubLogin;
	private String organization;
}

