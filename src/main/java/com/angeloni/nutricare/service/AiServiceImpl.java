package com.angeloni.nutricare.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angeloni.nutricare.dto.AiDto;
import com.angeloni.nutricare.repository.AiRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiServiceImpl implements AiService {
	
	@Autowired
	private AiRepository aiRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<AiDto> getAis() {
		return aiRepository.findAll().stream().map(x -> modelMapper.map(x, AiDto.class)).collect(Collectors.toList());
	}

}
