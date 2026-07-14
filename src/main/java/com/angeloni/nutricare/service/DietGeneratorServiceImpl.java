package com.angeloni.nutricare.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angeloni.nutricare.dto.DietRequestDto;

@Service
public class DietGeneratorServiceImpl implements DietGeneratorService {

	@Autowired
	private ClientService clientService;

	@Override
	public List<String> getClientsForSelection() {
		return clientService.getClients().stream()
				.map(c -> c.getName() + " " + c.getSurname())
				.collect(Collectors.toList());
	}

	@Override
	public String generateDiet(DietRequestDto request) {
		// TODO: implement full generation flow via DietGenerationService
		return "Diet generation started";
	}

}