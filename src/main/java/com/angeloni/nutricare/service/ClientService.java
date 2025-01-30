package com.angeloni.nutricare.service;

import java.util.List;

import javax.naming.AuthenticationException;

import com.angeloni.nutricare.dto.ClientDto;

public interface ClientService {
	
	public static final String CLIENT_ALREADY_PRESENT_FORMAT = "Client name: [%s], surname : [%s] is already present";
	
	ClientDto saveClient(ClientDto clientDto);
	
	List<ClientDto> getClients();
}
