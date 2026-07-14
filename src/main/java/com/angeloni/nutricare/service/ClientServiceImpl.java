package com.angeloni.nutricare.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.entity.ClientEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.exception.ConflictException;
import com.angeloni.nutricare.repository.ClientRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

	@Autowired
	private AuthService authService;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public ClientDto saveClient(ClientDto clientDto) {
		UserEntity user = authService.retrieveUserFromAuthentication();
		checkIfClientIsAlreadyPresent(clientDto, user);
		ClientEntity client = modelMapper.map(clientDto, ClientEntity.class);
		client.setUser(user);
		client = clientRepository.saveAndFlush(client);
		return modelMapper.map(client, ClientDto.class);
	}

	private void checkIfClientIsAlreadyPresent(ClientDto clientDto, UserEntity user) {
		Optional<ClientEntity> optionalClient = clientRepository.findByNameAndSurnameAndUser(clientDto.getName(),
				clientDto.getSurname(), user);
		if (optionalClient.isPresent()) {
			throw new ConflictException(String.format(ClientService.CLIENT_ALREADY_PRESENT_FORMAT, clientDto.getName(),
					clientDto.getSurname()));
		}
	}

	@Override
	public List<ClientDto> getClients() {
		UserEntity user = authService.retrieveUserFromAuthentication();
		return clientRepository.findByUser(user).stream().map(x -> modelMapper.map(x, ClientDto.class))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteClientById(Long id) {
		clientRepository.deleteById(id);
	}

}
