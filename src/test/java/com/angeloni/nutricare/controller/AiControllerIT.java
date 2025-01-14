package com.angeloni.nutricare.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;
import com.google.gson.reflect.TypeToken;

public class AiControllerIT extends AbstractControllerIT {

	private static final String ROOT_AI_CONTROLLER = "/ai";
	private static final String URI_AI_AIS = ROOT_AI_CONTROLLER + "/ais";

	@BeforeEach
	void setUp() {
		clearDb();
		UserEntity user = UserEntity.builder().username("testuser").email("testemail@gmail.com")
				.password("Test_passw_12345678").emailConfirmed(Boolean.TRUE).build();
		userRepository.saveAndFlush(user);
		UserDetails userDetails = User.withUsername("testuser").password("password").roles("USER").build();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
				userDetails.getPassword(), userDetails.getAuthorities());

		SecurityContext context = mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
		context.setAuthentication(authentication);
	}

	@AfterEach
	void tearDown() {
		clearDb();
	}

	
	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void givenCorrectRequest_whenGetAis_thenOKStatus200_ListOfAisReturned()
			throws Exception {
		// Given
		AiEntity ai1 = AiEntity.builder().id(1L).name(AINameEnum.CHATGPT).model(AIModelEnum.GPT4O).build();
		AiEntity ai2 = AiEntity.builder().id(2L).name(AINameEnum.CHATGPT).model(AIModelEnum.OPENAIO1).build();

		// Expected
		List<AiEntity> expectedAis = aiRepository.saveAll(List.of(ai1, ai2));

		// When
		MvcResult result = mockMvc
				.perform(get(URI_AI_AIS).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		// Then
		List<AiDto> actualResponse = gson.fromJson(result.getResponse().getContentAsString(),
				new TypeToken<List<AiDto>>() {
				}.getType());
		assertNotNull(actualResponse);
		assertEquals(expectedAis.size(), actualResponse.size());
		assertTrue(actualResponse.stream().anyMatch(x -> x.getName().equals(ai1.getName())));
		assertTrue(actualResponse.stream().anyMatch(x -> x.getModel().equals(ai1.getModel())));
	}
	
	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void givenCorrectRequest_whenGetAis_thenOKStatus200_EmptyListReturned()
			throws Exception {
		// Expected
		List<AiEntity> expectedAis = Collections.emptyList();

		// When
		MvcResult result = mockMvc
				.perform(get(URI_AI_AIS).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		// Then
		List<AiDto> actualResponse = gson.fromJson(result.getResponse().getContentAsString(),
				new TypeToken<List<AiDto>>() {
				}.getType());
		assertNotNull(actualResponse);
		assertEquals(expectedAis.size(), actualResponse.size());
		assertTrue(actualResponse.isEmpty());
	}
}
