package com.angeloni.nutricare.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.service.ClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/client")
public class ClientController {

	@Autowired
	private ClientService clientService;

	@Operation(summary = "create a client")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "create a new client", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ClientDto.class))) }) })
	@PostMapping("/create")
	public ResponseEntity<ClientDto> createDiet(@Valid @RequestBody ClientDto clientDto) {
		return new ResponseEntity<>(clientService.createClient(clientDto), HttpStatus.OK);
	}
}
