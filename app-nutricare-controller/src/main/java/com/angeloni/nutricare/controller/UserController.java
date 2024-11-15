package com.angeloni.nutricare.controller;

import javax.validation.Valid;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angeloni.nutricare.dto.LoginDto;
import com.angeloni.nutricare.dto.UserDto;
import com.angeloni.nutricare.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(summary = "register a new user")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "user registrated succesfully", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class))) }) })
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

}
