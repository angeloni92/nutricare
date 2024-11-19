package com.angeloni.nutricare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import com.angeloni.nutricare.dto.LoginDto;
import com.angeloni.nutricare.dto.UserDto;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.UserRoleEnum;
import com.angeloni.nutricare.exception.ErrorDetails;
import com.angeloni.nutricare.service.UserService;
import com.google.gson.reflect.TypeToken;

public class UserControllerIT extends AbstractControllerIT {

	private static final String ROOT_USER_CONTROLLER = "/auth";
	private static final String URI_USER_REGISTER = ROOT_USER_CONTROLLER + "/register";
	private static final String URI_USER_LOGIN = ROOT_USER_CONTROLLER + "/login";
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder; 

	@BeforeEach
	void setUp() {
		clearDb();
	}

	@AfterEach
	void tearDown() {
		clearDb();
	}

	/*
	 * REGISTER
	 */
	@Test
	void givenValidUserDto_whenRegister_thenOKStatus200_RegistrationSuccessfull() throws Exception {
		// Given
		UserDto userDto = new UserDto();
		userDto.setUsername("username");
		userDto.setEmail("testemail@email.com");
		userDto.setPassword("Test_Password_123456");

		String request = gson.toJson(userDto);

		// Expected
		String expectedResponse = UserService.REGISTRATION_SUCCESS;
		UserRoleEnum expectedUserRole = UserRoleEnum.USER;

		// When
		MvcResult result = mockMvc
				.perform(post(URI_USER_REGISTER).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk()).andReturn();

		// Then
		String actualResponse = result.getResponse().getContentAsString();
		Optional<UserEntity> actualUser = userRepository.findByUsername(userDto.getUsername());

		assertNotNull(actualResponse);
		assertEquals(expectedResponse, actualResponse);
		assertTrue(actualUser.isPresent());
		assertEquals(userDto.getEmail(), actualUser.get().getEmail());
		assertEquals(expectedUserRole, actualUser.get().getRole());
		assertNotNull(actualUser.get().getCreatedAt());
		assertNotNull(actualUser.get().getUpdatedAt());
	}
	
	@Test
	void givenValidUserDtoAndUsernameAlreadyPresent_whenRegister_thenOKStatus200_UsernameAlreadyExists() throws Exception {
		// Given
		UserDto userDto = new UserDto();
		userDto.setUsername("username");
		userDto.setEmail("testemail@email.com");
		userDto.setPassword("Test_Password_123456");
		
		UserEntity user = modelMapper.map(userDto, UserEntity.class);
		userRepository.saveAndFlush(user);

		String request = gson.toJson(userDto);

		// Expected
		String expectedResponse = UserService.USERNAME_ALREADY_EXISTS;

		// When
		MvcResult result = mockMvc
				.perform(post(URI_USER_REGISTER).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk()).andReturn();

		// Then
		String actualResponse = result.getResponse().getContentAsString();

		assertNotNull(actualResponse);
		assertEquals(expectedResponse, actualResponse);
	}
	
	@Test
	void givenValidUserDtoAndEmailAlreadyPresent_whenRegister_thenOKStatus200_EmailAlreadyExists() throws Exception {
		// Given
		UserDto userDto = new UserDto();
		userDto.setUsername("username");
		userDto.setEmail("testemail@email.com");
		userDto.setPassword("Test_Password_123456");
		
		UserEntity user = modelMapper.map(userDto, UserEntity.class);
		String differentUsername = "differentUsername";
		user.setUsername(differentUsername);
		userRepository.saveAndFlush(user);

		String request = gson.toJson(userDto);

		// Expected
		String expectedResponse = UserService.EMAIL_ALREADY_EXISTS;

		// When
		MvcResult result = mockMvc
				.perform(post(URI_USER_REGISTER).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk()).andReturn();

		// Then
		String actualResponse = result.getResponse().getContentAsString();

		assertNotNull(actualResponse);
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void givenInvalidUserDto_whenRegister_thenKOStatus400_BadRequest() throws Exception {
		// Given
		UserDto userDto = new UserDto();
		userDto.setUsername("");
		userDto.setEmail("invalid email");
		userDto.setPassword("123");

		String request = gson.toJson(userDto);

		// Expected
		List<String> expectedErrorMessages = new ArrayList<>();
		String expectedErrorMessagePasswordPattern = "Password must contain at least one uppercase letter, one number, and one special character";
		expectedErrorMessages.add(expectedErrorMessagePasswordPattern);
		String expectedErrorMessagePasswordSize = "Password must be at least 6 characters long";
		expectedErrorMessages.add(expectedErrorMessagePasswordSize);
		String expectedErrorMessageUsernameSize = "Username must be between 3 and 50 characters";
		expectedErrorMessages.add(expectedErrorMessageUsernameSize);
		String expectedErrorMessageEmailNotValid = "Email must be valid";
		expectedErrorMessages.add(expectedErrorMessageEmailNotValid);
		String expectedErrorMessageUsernameNotBlank = "Username is required";
		expectedErrorMessages.add(expectedErrorMessageUsernameNotBlank);
		
		// When
		MvcResult result = mockMvc.perform(post(URI_USER_REGISTER).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isBadRequest()).andReturn();
		
		List<ErrorDetails> errorDetailsList = gson.fromJson(result.getResponse().getContentAsString(), new TypeToken<List<ErrorDetails>>() {}.getType());
		assertEquals(expectedErrorMessages.size(),errorDetailsList.size());
		assertThat(errorDetailsList.stream().anyMatch(x -> x.getMessage().equals(expectedErrorMessagePasswordPattern)));
		assertThat(errorDetailsList.stream().anyMatch(x -> x.getMessage().equals(expectedErrorMessagePasswordSize)));
		assertThat(errorDetailsList.stream().anyMatch(x -> x.getMessage().equals(expectedErrorMessageUsernameSize)));
		assertThat(errorDetailsList.stream().anyMatch(x -> x.getMessage().equals(expectedErrorMessageEmailNotValid)));
		assertThat(errorDetailsList.stream().anyMatch(x -> x.getMessage().equals(expectedErrorMessageUsernameNotBlank)));
	}
	
	/*
	 * LOGIN
	 */
	@Test
	void givenValidLoginDto_whenLogin_thenOKStatus200_TokenGenerated() throws Exception {
		// Given
		UserEntity user = new UserEntity();
		user.setUsername("username");
		user.setEmail("testemail@email.com");
		String expectedPassword = "Test_Password_123456";
		user.setPassword(passwordEncoder.encode(expectedPassword));
		user = userRepository.saveAndFlush(user);
		
		LoginDto loginDto = new LoginDto();
		loginDto.setLogin(user.getUsername());
		loginDto.setPassword(expectedPassword);

		String request = gson.toJson(loginDto);
		
		// When
		MvcResult result = mockMvc
				.perform(post(URI_USER_LOGIN).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk()).andReturn();

		// Then
		String actualResponse = result.getResponse().getContentAsString();

		assertNotNull(actualResponse);
		assertThat(actualResponse.startsWith("Bearer"));
	}
	
	@Test
	void givenValidLoginDtoAndUserNotRegistered_whenLogin_thenKOStatus401_Unauthorized() throws Exception {
		// Given
		LoginDto loginDto = new LoginDto();
		loginDto.setLogin("username");
		loginDto.setPassword("Test_Password_123456");

		String request = gson.toJson(loginDto);
		
		//Expected
		String expectedErrorMessage = UserService.INVALID_CREDENTIAL_ERROR_MESSAGE;
		
		// When
		MvcResult result = mockMvc
				.perform(post(URI_USER_LOGIN).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isUnauthorized()).andReturn();

		// Then
		ErrorDetails errorDetails = gson.fromJson(result.getResponse().getContentAsString(), new TypeToken<ErrorDetails>() {}.getType());

		assertNotNull(errorDetails);
		assertEquals(expectedErrorMessage, errorDetails.getMessage());
	}
	
	@Test
	void givenInvalidLoginDto_whenLogin_thenKOStatus400_BadRequest() throws Exception {
		// Given
		LoginDto loginDto = new LoginDto();
		loginDto.setLogin("");

		String request = gson.toJson(loginDto);
		
		//Expected
		List <String> expectedErrorMessages = new ArrayList<>();
		String expectedErrorMessageInvalidCredential = UserService.INVALID_CREDENTIAL_ERROR_MESSAGE;
		expectedErrorMessages.add(expectedErrorMessageInvalidCredential);
		String expectedErrorMessageLoginSize = "Username or Email must be between 3 and 50 characters";
		expectedErrorMessages.add(expectedErrorMessageLoginSize);
		String expectedErrorMessagePasswordNotBlank = "Password is required";
		expectedErrorMessages.add(expectedErrorMessagePasswordNotBlank);
		
		// When
		MvcResult result = mockMvc
				.perform(post(URI_USER_LOGIN).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isBadRequest()).andReturn();

		// Then
		List<ErrorDetails> errorDetailsList = gson.fromJson(result.getResponse().getContentAsString(), new TypeToken<List<ErrorDetails>>() {}.getType());

		assertNotNull(errorDetailsList);
		assertEquals(expectedErrorMessages.size(), errorDetailsList.size());
		assertThat(errorDetailsList.stream().anyMatch(x -> x.getMessage().equals(expectedErrorMessageInvalidCredential)));
		assertThat(errorDetailsList.stream().anyMatch(x -> x.getMessage().equals(expectedErrorMessageLoginSize)));
		assertThat(errorDetailsList.stream().anyMatch(x -> x.getMessage().equals(expectedErrorMessagePasswordNotBlank)));
	}
}
