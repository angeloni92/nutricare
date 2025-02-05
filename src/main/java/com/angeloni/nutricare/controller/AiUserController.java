package com.angeloni.nutricare.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angeloni.nutricare.dto.AiUserDto;
import com.angeloni.nutricare.service.AiUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@Validated
@RequestMapping("/aiuser")
public class AiUserController {

	@Autowired
	private AiUserService aiUserService;
	
	@Operation(summary = "ai by user")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ai by user", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AiUserDto.class))) }) })
	@GetMapping
	public ResponseEntity<AiUserDto> getAiByUser() {
		return new ResponseEntity<>(aiUserService.getByUser(), HttpStatus.OK);
	}
}
