package com.angeloni.nutricare.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.angeloni.nutricare.dto.LoginDto;
import com.angeloni.nutricare.dto.UserDto;
import com.angeloni.nutricare.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(summary = "register a new user")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "confirm to complete registration", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class))) }) })
	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody UserDto userDto) {
		return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.OK);
	}

	@Operation(summary = "login user")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "user logged succesfully", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class))) }),
			@ApiResponse(responseCode = "401", description = "invalid credentials")})
	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestBody LoginDto loginDto) {
		return new ResponseEntity<>(UserService.BEARER + userService.loginUser(loginDto), HttpStatus.OK);
	}
	
	@Operation(summary = "confirm user")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "user registered successfully!", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class))) }) })
	@GetMapping("/confirm")
    public ResponseEntity<String> confirmUserEmail(@RequestParam("token") String token) {
        String message = userService.confirmEmail(token);
        return ResponseEntity.ok(message);
    }

}
