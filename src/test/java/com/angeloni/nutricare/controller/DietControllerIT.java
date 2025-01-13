package com.angeloni.nutricare.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.CircumferenceDto;
import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.dto.ClientRequestDto;
import com.angeloni.nutricare.dto.CommonResponseDto;
import com.angeloni.nutricare.dto.DietDetailDto;
import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.dto.FoldDto;
import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;
import com.angeloni.nutricare.enums.ActivityLevelEnum;
import com.angeloni.nutricare.enums.DietaryPreferenceEnum;
import com.angeloni.nutricare.enums.PrimaryGoalEnum;
import com.angeloni.nutricare.exception.ErrorDetails;
import com.angeloni.nutricare.service.DietService;
import com.google.gson.reflect.TypeToken;

public class DietControllerIT extends AbstractControllerIT {

	private static final String ROOT_DIET_CONTROLLER = "/diet";
	private static final String URI_DIET_GENERATE = ROOT_DIET_CONTROLLER + "/generate";

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

	/*
	 * GENERATE
	 */
	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void givenValidDietRequestDto_whenGenerateDiet_thenOKStatus200_StartGenerateDiet() throws Exception {
		// Given
		String expectedAiKey = "test_ai_key_1234";
		DietRequestDto dietRequestDto = new DietRequestDto();
		AiDto aiDto = new AiDto();
		aiDto.setName(AINameEnum.CHATGPT);
		aiDto.setModel(AIModelEnum.GPT4O);
		aiDto.setAiKey(expectedAiKey);
		dietRequestDto.setAi(aiDto);
		ClientRequestDto clientRequestDto = new ClientRequestDto();
		ClientDto clientDto = new ClientDto();
		AnthropometryDto anthropometryDto = new AnthropometryDto();
		anthropometryDto.setHeight(Integer.valueOf(176));
		anthropometryDto.setWeight(Double.valueOf("68.7"));
		CircumferenceDto circumferenceDto = new CircumferenceDto();
		circumferenceDto.setArm(Integer.valueOf(200));
		circumferenceDto.setChest(Integer.valueOf(300));
		circumferenceDto.setChest(Integer.valueOf(400));
		circumferenceDto.setThigh(Integer.valueOf(324));
		circumferenceDto.setWaist(Integer.valueOf(156));
		anthropometryDto.setCircumference(circumferenceDto);
		FoldDto foldDto = new FoldDto();
		foldDto.setAbdominal(Integer.valueOf(167));
		foldDto.setAxillary(Integer.valueOf(343));
		foldDto.setPectoral(Integer.valueOf(632));
		foldDto.setSubscapolaris(Integer.valueOf(422));
		foldDto.setSuprailiac(Integer.valueOf(955));
		foldDto.setThigh(Integer.valueOf(343));
		foldDto.setTriceps(Integer.valueOf(721));
		anthropometryDto.setFold(foldDto);
		clientRequestDto.setAnthropometry(anthropometryDto);
		clientDto.setName("Pippo");
		clientDto.setSurname("Costanzo");
		clientDto.setAge(Integer.valueOf(32));
		clientDto.setCountry("ITALY");
		clientRequestDto.setClient(clientDto);
		DietDetailDto dietDetailDto = new DietDetailDto();
		dietDetailDto.setActivityLevel(ActivityLevelEnum.MODERATE);
		dietDetailDto.setCaloryTarget(Integer.valueOf(500));
		dietDetailDto.setDietaryPreference(DietaryPreferenceEnum.OMNIVORE);
		dietDetailDto.setFreeDay(DayOfWeek.SATURDAY);
		dietDetailDto.setMonth(Month.APRIL);
		dietDetailDto.setPrimaryGoal(PrimaryGoalEnum.MUSCLE_GAIN);
		clientRequestDto.setDietDetail(dietDetailDto);
		dietRequestDto.setClientRequest(clientRequestDto);
		
		AiEntity ai = new AiEntity();
		ai.setId(1L);
		ai.setName(aiDto.getName());
		ai.setModel(aiDto.getModel());		
		aiRepository.saveAndFlush(ai);
		
		String request = gson.toJson(dietRequestDto);

		// Expected
		String expectedResponse = DietService.START_GENERATE_DIET_MSG;

		// When
		MvcResult result = mockMvc
				.perform(post(URI_DIET_GENERATE).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk()).andReturn();

		// Then
		CommonResponseDto actualResponse = gson.fromJson(result.getResponse().getContentAsString(), new TypeToken<CommonResponseDto>() {}.getType());
		Mockito.verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(),Mockito.any(Object.class));
		Optional<AiUserEntity> actualAiUser = aiUserRepository.findByAiKey(expectedAiKey);
		assertNotNull(actualResponse);
		assertEquals(expectedResponse, actualResponse.getMessage());
		assertTrue(actualAiUser.isPresent());
		assertEquals(expectedAiKey, actualAiUser.get().getAiKey());
	}
	
	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void givenValidDietRequestDtoAndUserNotRegistrated_whenGenerateDiet_thenKOStatus404_NotFoundException() throws Exception {
		// Given
		userRepository.deleteAll();
		
		String expectedAiKey = "test_ai_key_1234";
		DietRequestDto dietRequestDto = new DietRequestDto();
		AiDto aiDto = new AiDto();
		aiDto.setName(AINameEnum.CHATGPT);
		aiDto.setModel(AIModelEnum.GPT4O);
		aiDto.setAiKey(expectedAiKey);
		dietRequestDto.setAi(aiDto);
		ClientRequestDto clientRequestDto = new ClientRequestDto();
		ClientDto clientDto = new ClientDto();
		AnthropometryDto anthropometryDto = new AnthropometryDto();
		anthropometryDto.setHeight(Integer.valueOf(176));
		anthropometryDto.setWeight(Double.valueOf("68.7"));
		CircumferenceDto circumferenceDto = new CircumferenceDto();
		circumferenceDto.setArm(Integer.valueOf(200));
		circumferenceDto.setChest(Integer.valueOf(300));
		circumferenceDto.setChest(Integer.valueOf(400));
		circumferenceDto.setThigh(Integer.valueOf(324));
		circumferenceDto.setWaist(Integer.valueOf(156));
		anthropometryDto.setCircumference(circumferenceDto);
		FoldDto foldDto = new FoldDto();
		foldDto.setAbdominal(Integer.valueOf(167));
		foldDto.setAxillary(Integer.valueOf(343));
		foldDto.setPectoral(Integer.valueOf(632));
		foldDto.setSubscapolaris(Integer.valueOf(422));
		foldDto.setSuprailiac(Integer.valueOf(955));
		foldDto.setThigh(Integer.valueOf(343));
		foldDto.setTriceps(Integer.valueOf(721));
		anthropometryDto.setFold(foldDto);
		clientRequestDto.setAnthropometry(anthropometryDto);
		clientDto.setName("Pippo");
		clientDto.setSurname("Costanzo");
		clientDto.setAge(Integer.valueOf(32));
		clientDto.setCountry("ITALY");
		clientRequestDto.setClient(clientDto);
		DietDetailDto dietDetailDto = new DietDetailDto();
		dietDetailDto.setActivityLevel(ActivityLevelEnum.MODERATE);
		dietDetailDto.setCaloryTarget(Integer.valueOf(500));
		dietDetailDto.setDietaryPreference(DietaryPreferenceEnum.OMNIVORE);
		dietDetailDto.setFreeDay(DayOfWeek.SATURDAY);
		dietDetailDto.setMonth(Month.APRIL);
		dietDetailDto.setPrimaryGoal(PrimaryGoalEnum.MUSCLE_GAIN);
		clientRequestDto.setDietDetail(dietDetailDto);
		dietRequestDto.setClientRequest(clientRequestDto);
		
		String request = gson.toJson(dietRequestDto);

		// Expected 
		String expectedErrorDetails = String.format(DietService.USER_NOT_FOUND_FORMAT, "testuser");

		// When
		MvcResult result = mockMvc
				.perform(post(URI_DIET_GENERATE).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isNotFound()).andReturn();

		// Then
		ErrorDetails actualErrorDetails = gson.fromJson(result.getResponse().getContentAsString(), new TypeToken<ErrorDetails>() {}.getType());
		Mockito.verify(jmsTemplate, never()).convertAndSend(Mockito.anyString(),Mockito.any(Object.class));

		assertNotNull(actualErrorDetails);
		assertEquals(expectedErrorDetails, actualErrorDetails.getMessage());
	}
	
	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void givenValidDietRequestDtoAiNotPresent_whenGenerateDiet_thenKOStatus404_NotFoundException() throws Exception {
		// Given
		String expectedAiKey = "test_ai_key_1234";
		DietRequestDto dietRequestDto = new DietRequestDto();
		AiDto aiDto = new AiDto();
		aiDto.setName(AINameEnum.CHATGPT);
		aiDto.setModel(AIModelEnum.GPT4O);
		aiDto.setAiKey(expectedAiKey);
		dietRequestDto.setAi(aiDto);
		ClientRequestDto clientRequestDto = new ClientRequestDto();
		ClientDto clientDto = new ClientDto();
		AnthropometryDto anthropometryDto = new AnthropometryDto();
		anthropometryDto.setHeight(Integer.valueOf(176));
		anthropometryDto.setWeight(Double.valueOf("68.7"));
		CircumferenceDto circumferenceDto = new CircumferenceDto();
		circumferenceDto.setArm(Integer.valueOf(200));
		circumferenceDto.setChest(Integer.valueOf(300));
		circumferenceDto.setChest(Integer.valueOf(400));
		circumferenceDto.setThigh(Integer.valueOf(324));
		circumferenceDto.setWaist(Integer.valueOf(156));
		anthropometryDto.setCircumference(circumferenceDto);
		FoldDto foldDto = new FoldDto();
		foldDto.setAbdominal(Integer.valueOf(167));
		foldDto.setAxillary(Integer.valueOf(343));
		foldDto.setPectoral(Integer.valueOf(632));
		foldDto.setSubscapolaris(Integer.valueOf(422));
		foldDto.setSuprailiac(Integer.valueOf(955));
		foldDto.setThigh(Integer.valueOf(343));
		foldDto.setTriceps(Integer.valueOf(721));
		anthropometryDto.setFold(foldDto);
		clientRequestDto.setAnthropometry(anthropometryDto);
		clientDto.setName("Pippo");
		clientDto.setSurname("Costanzo");
		clientDto.setAge(Integer.valueOf(32));
		clientDto.setCountry("ITALY");
		clientRequestDto.setClient(clientDto);
		DietDetailDto dietDetailDto = new DietDetailDto();
		dietDetailDto.setActivityLevel(ActivityLevelEnum.MODERATE);
		dietDetailDto.setCaloryTarget(Integer.valueOf(500));
		dietDetailDto.setDietaryPreference(DietaryPreferenceEnum.OMNIVORE);
		dietDetailDto.setFreeDay(DayOfWeek.SATURDAY);
		dietDetailDto.setMonth(Month.APRIL);
		dietDetailDto.setPrimaryGoal(PrimaryGoalEnum.MUSCLE_GAIN);
		clientRequestDto.setDietDetail(dietDetailDto);
		dietRequestDto.setClientRequest(clientRequestDto);
		String request = gson.toJson(dietRequestDto);

		// Expected 
		String expectedErrorDetails = String.format(DietService.AI_NOT_FOUND_FORMAT, aiDto.getName().getValue(), aiDto.getModel());

		// When
		MvcResult result = mockMvc
				.perform(post(URI_DIET_GENERATE).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isNotFound()).andReturn();

		// Then
		ErrorDetails actualErrorDetails = gson.fromJson(result.getResponse().getContentAsString(), new TypeToken<ErrorDetails>() {}.getType());
		Mockito.verify(jmsTemplate, never()).convertAndSend(Mockito.anyString(),Mockito.any(Object.class));

		assertNotNull(actualErrorDetails);
		assertEquals(expectedErrorDetails, actualErrorDetails.getMessage());
	}
}
