package com.angeloni.nutricare.dto;

import lombok.Data;

@Data
public class CopilotConnectionStatusDto {

	private Boolean connected;
	private String githubLogin;
	private String organization;
	private String scope;
}

