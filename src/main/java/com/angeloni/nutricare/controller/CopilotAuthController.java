package com.angeloni.nutricare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.angeloni.nutricare.dto.CommonResponseDto;
import com.angeloni.nutricare.dto.CopilotAuthResultDto;
import com.angeloni.nutricare.dto.CopilotAuthStartDto;
import com.angeloni.nutricare.dto.CopilotConnectionStatusDto;
import com.angeloni.nutricare.service.CopilotAuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
@RequestMapping("/auth/copilot")
public class CopilotAuthController {

	@Autowired
	private CopilotAuthService copilotAuthService;

	@Operation(summary = "start github copilot oauth")
	@GetMapping("/start")
	public ResponseEntity<CopilotAuthStartDto> startAuthorization() {
		return new ResponseEntity<>(copilotAuthService.startAuthorization(), HttpStatus.OK);
	}

	@Operation(summary = "oauth callback for github copilot")
	@GetMapping("/callback")
	public ResponseEntity<CopilotAuthResultDto> completeAuthorization(@RequestParam("code") @NotBlank String code,
			@RequestParam("state") @NotBlank String state) {
		return new ResponseEntity<>(copilotAuthService.completeAuthorization(code, state), HttpStatus.OK);
	}

	@Operation(summary = "get copilot connection status")
	@GetMapping("/status")
	public ResponseEntity<CopilotConnectionStatusDto> getConnectionStatus() {
		return new ResponseEntity<>(copilotAuthService.getCurrentConnectionStatus(), HttpStatus.OK);
	}

	@Operation(summary = "disconnect github copilot account")
	@DeleteMapping
	public ResponseEntity<CommonResponseDto> disconnect() {
		copilotAuthService.disconnectCurrentUser();
		CommonResponseDto response = new CommonResponseDto();
		response.setStatus("Success");
		response.setMessage(CopilotAuthService.COPILOT_UNLINKED);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

