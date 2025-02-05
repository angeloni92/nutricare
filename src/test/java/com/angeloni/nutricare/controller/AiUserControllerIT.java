package com.angeloni.nutricare.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import com.angeloni.nutricare.dto.AiUserDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;
import com.google.gson.reflect.TypeToken;

public class AiUserControllerIT extends AbstractControllerIT {

	private static final String ROOT_AIUSER_CONTROLLER = "/aiuser";
	private static final String URI_AIUSER = ROOT_AIUSER_CONTROLLER;

	@BeforeEach
	void setUp() {
		clearDb();
		UserEntity user = UserEntity.builder().username("testuser").email("testemail@gmail.com")
				.password("Test_passw_12345678").emailConfirmed(Boolean.TRUE).build();
		userRepository.saveAndFlush(user);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), null);
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@AfterEach
	void tearDown() {
		clearDb();
	}

	
	@Test
	@WithMockUser(username = "testuser")
	void givenCorrectRequest_whenGetAiByUser_thenOKStatus200_AiByUserReturned()
			throws Exception {
		// Given
		String expectedAiKey = "testaikey";
		AiEntity ai = AiEntity.builder().id(1L).name(AINameEnum.CHATGPT).model(AIModelEnum.GPT4O).build();
		aiRepository.saveAndFlush(ai);
		UserEntity user = userRepository.findAll().stream().findFirst().get();
		AiUserEntity expectedAiUser = AiUserEntity.builder().ai(ai).user(user).aiKey(expectedAiKey).build();
		expectedAiUser = aiUserRepository.saveAndFlush(expectedAiUser);

		// When
		MvcResult result = mockMvc
				.perform(get(URI_AIUSER).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		// Then
		AiUserDto actualResponse = gson.fromJson(result.getResponse().getContentAsString(),
				new TypeToken<AiUserDto>() {
				}.getType());
		assertNotNull(actualResponse);
		assertEquals(expectedAiUser.getAi().getId(), actualResponse.getAi().getId());
		assertEquals(expectedAiUser.getAi().getName(), actualResponse.getAi().getName());
		assertEquals(expectedAiUser.getAi().getModel(), actualResponse.getAi().getModel());
		assertEquals(expectedAiUser.getUser().getUsername(), actualResponse.getUser().getUsername());
		assertEquals(expectedAiUser.getAiKey(), actualResponse.getAiKey());
	}
	
	@Test
	@WithMockUser(username = "testuser")
	void givenCorrectRequest_whenGetAiByUser_thenOKStatus200_AiByUserEmpty()
			throws Exception {
		// Given
		AiEntity ai = AiEntity.builder().id(1L).name(AINameEnum.CHATGPT).model(AIModelEnum.GPT4O).build();
		aiRepository.saveAndFlush(ai);

		// When
		MvcResult result = mockMvc
				.perform(get(URI_AIUSER).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		// Then
		AiUserDto actualResponse = gson.fromJson(result.getResponse().getContentAsString(),
				new TypeToken<AiUserDto>() {
				}.getType());
		assertNotNull(actualResponse);
		assertNull(actualResponse.getAi());
		assertNull(actualResponse.getUser());
		assertNull(actualResponse.getAiKey());
	}
}
