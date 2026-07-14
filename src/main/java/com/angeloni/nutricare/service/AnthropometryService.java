package com.angeloni.nutricare.service;

import java.util.List;

import com.angeloni.nutricare.dto.AnthropometryDto;

public interface AnthropometryService {

	AnthropometryDto saveVisit(Long clientId, AnthropometryDto dto);

	List<AnthropometryDto> getVisitsByClient(Long clientId);
}
