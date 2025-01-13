package com.angeloni.nutricare.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.entity.ClientEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.exception.ErrorDetails;
import com.angeloni.nutricare.service.ClientService;
import com.google.gson.reflect.TypeToken;

public class ClientControllerIT extends AbstractControllerIT {

	private static final String ROOT_CLIENT_CONTROLLER = "/client";
	private static final String URI_CLIENT_SAVE = ROOT_CLIENT_CONTROLLER;
	private static final String URI_CLIENT_CLIENTS = ROOT_CLIENT_CONTROLLER + "/clients";

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
	 * SAVE
	 */
	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void givenValidClientDto_whenSaveClient_thenOKStatus200_ClientSaved() throws Exception {
		// Given
		ClientDto clientDto = new ClientDto();
		clientDto.setName("Paolo");
		clientDto.setSurname("Rossi");
		clientDto.setAge(Integer.valueOf("32"));
		clientDto.setCountry("Italia");
		List<String> expectedHealtConditions = new ArrayList<>();
		String expectedHealthCondition = "diabete";
		expectedHealtConditions.add(expectedHealthCondition);
		clientDto.setHealthConditions(expectedHealtConditions);
		List<String> expectedAllergies = new ArrayList<>();
		String expectedAllergie1 = "arachidi";
		String expectedAllergie2 = "senape";
		expectedAllergies.addAll(List.of(expectedAllergie1, expectedAllergie2));
		clientDto.setAllergies(expectedAllergies);

		String request = gson.toJson(clientDto);

		// When
		MvcResult result = mockMvc
				.perform(post(URI_CLIENT_SAVE).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk()).andReturn();

		// Then
		ClientDto actualResponse = gson.fromJson(result.getResponse().getContentAsString(), new TypeToken<ClientDto>() {
		}.getType());
		Optional<ClientEntity> optionalClient = clientRepository.findAll().stream().findFirst();
		assertTrue(optionalClient.isPresent());
		ClientEntity actualClient = optionalClient.get();
		assertNotNull(actualClient.getUser());
		assertNotNull(actualResponse);

		assertEquals(actualResponse.getName(), actualClient.getName());
		assertEquals(actualResponse.getSurname(), actualClient.getSurname());
		assertEquals(actualResponse.getAge(), actualClient.getAge());
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void givenValidClientDtoAndClientAlreadyPresent_whenSaveClient_thenKOStatus409_ConflictException()
			throws Exception {
		// Given
		ClientDto clientDto = new ClientDto();
		clientDto.setName("Paolo");
		clientDto.setSurname("Rossi");
		clientDto.setAge(Integer.valueOf("32"));
		clientDto.setCountry("Italia");
		List<String> expectedHealtConditions = new ArrayList<>();
		String expectedHealthCondition = "diabete";
		expectedHealtConditions.add(expectedHealthCondition);
		clientDto.setHealthConditions(expectedHealtConditions);
		List<String> expectedAllergies = new ArrayList<>();
		String expectedAllergie1 = "arachidi";
		String expectedAllergie2 = "senape";
		expectedAllergies.addAll(List.of(expectedAllergie1, expectedAllergie2));
		clientDto.setAllergies(expectedAllergies);

		UserEntity user = userRepository.findAll().stream().findFirst().get();
		ClientEntity client = ClientEntity.builder().name(clientDto.getName()).surname(clientDto.getSurname())
				.age(clientDto.getAge()).country(clientDto.getCountry()).user(user).build();
		clientRepository.save(client);

		String request = gson.toJson(clientDto);

		// Expected
		String expectedErrorDetails = String.format(ClientService.CLIENT_ALREADY_PRESENT_FORMAT,clientDto.getName(), clientDto.getSurname());

		// When
		MvcResult result = mockMvc
				.perform(post(URI_CLIENT_SAVE).contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isConflict()).andReturn();

		// Then
		ErrorDetails actualErrorDetails = gson.fromJson(result.getResponse().getContentAsString(),
				new TypeToken<ErrorDetails>() {
				}.getType());
		assertNotNull(actualErrorDetails);
		assertEquals(expectedErrorDetails, actualErrorDetails.getMessage());
	}
	
	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void givenCorrectRequest_whenGetClients_thenOKStatus200_ListOfClientsReturned()
			throws Exception {
		// Given
		UserEntity user = userRepository.findAll().stream().findFirst().get();
		ClientEntity client1 = ClientEntity.builder().name("Paolo").surname("Rossi")
				.age(Integer.valueOf("34")).country("Italia").user(user).build();
		ClientEntity client2 = ClientEntity.builder().name("Enzo").surname("Bianchi")
				.age(Integer.valueOf("26")).country("Inghilterra").user(user).build();

		// Expected
		List<ClientEntity> expectedClients = clientRepository.saveAll(List.of(client1, client2));

		// When
		MvcResult result = mockMvc
				.perform(get(URI_CLIENT_CLIENTS).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		// Then
		List<ClientDto> actualResponse = gson.fromJson(result.getResponse().getContentAsString(),
				new TypeToken<List<ClientDto>>() {
				}.getType());
		assertNotNull(actualResponse);
		assertEquals(expectedClients.size(), actualResponse.size());
		assertTrue(actualResponse.stream().anyMatch(x -> x.getName().equals(client1.getName())));
		assertTrue(actualResponse.stream().anyMatch(x -> x.getSurname().equals(client1.getSurname())));
		assertTrue(actualResponse.stream().anyMatch(x -> x.getAge() == client1.getAge()));
		assertTrue(actualResponse.stream().anyMatch(x -> x.getCountry().equals(client1.getCountry())));
		assertTrue(actualResponse.stream().anyMatch(x -> x.getName().equals(client2.getName())));
		assertTrue(actualResponse.stream().anyMatch(x -> x.getSurname().equals(client2.getSurname())));
		assertTrue(actualResponse.stream().anyMatch(x -> x.getAge() == client2.getAge()));
		assertTrue(actualResponse.stream().anyMatch(x -> x.getCountry().equals(client2.getCountry())));
	}
	
	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void givenCorrectRequest_whenGetClients_thenOKStatus200_EmptyListReturned()
			throws Exception {
		// Expected
		List<ClientEntity> expectedClients = Collections.emptyList();

		// When
		MvcResult result = mockMvc
				.perform(get(URI_CLIENT_CLIENTS).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		// Then
		List<ClientDto> actualResponse = gson.fromJson(result.getResponse().getContentAsString(),
				new TypeToken<List<ClientDto>>() {
				}.getType());
		assertNotNull(actualResponse);
		assertEquals(expectedClients.size(), actualResponse.size());
		assertTrue(actualResponse.isEmpty());
	}
}
