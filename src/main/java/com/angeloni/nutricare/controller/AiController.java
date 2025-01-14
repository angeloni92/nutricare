package com.angeloni.nutricare.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.service.AiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@Validated
@RequestMapping("/ai")
public class AiController {

	@Autowired
	private AiService aiService;
	
	@Operation(summary = "list of ai")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "list of ai", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AiDto.class))) }) })
	@GetMapping("/ais")
	public ResponseEntity<List<AiDto>> getAis() {
		return new ResponseEntity<>(aiService.getAis(), HttpStatus.OK);
	}
}
