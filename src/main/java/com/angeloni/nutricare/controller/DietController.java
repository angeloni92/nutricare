package com.angeloni.nutricare.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.service.DietService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/diet")
public class DietController {

	@Autowired
	private DietService dietService;

	@Operation(summary = "generate a new diet")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "generate a new diet for a client", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class))) }) })
	@PostMapping("/generate")
	public ResponseEntity<String> register(@Valid @RequestBody DietRequestDto dietRequestDto) {
		return new ResponseEntity<>(dietService.generateDiet(dietRequestDto), HttpStatus.OK);
	}
}
