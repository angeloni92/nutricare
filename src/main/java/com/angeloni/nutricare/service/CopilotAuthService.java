package com.angeloni.nutricare.service;

import com.angeloni.nutricare.dto.CopilotAuthResultDto;
import com.angeloni.nutricare.dto.CopilotAuthStartDto;
import com.angeloni.nutricare.dto.CopilotConnectionStatusDto;
import com.angeloni.nutricare.entity.UserEntity;

public interface CopilotAuthService {

	String COPILOT_LINKED = "Copilot account linked successfully";
	String COPILOT_UNLINKED = "Copilot account disconnected";

	CopilotAuthStartDto startAuthorization();

	CopilotAuthResultDto completeAuthorization(String code, String state);

	CopilotConnectionStatusDto getCurrentConnectionStatus();

	void disconnectCurrentUser();

	String resolveAccessTokenForUser(UserEntity user);
}

