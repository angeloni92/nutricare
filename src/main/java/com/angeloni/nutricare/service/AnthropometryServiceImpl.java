package com.angeloni.nutricare.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.CircumferenceDto;
import com.angeloni.nutricare.dto.FoldDto;
import com.angeloni.nutricare.entity.AnthropometryEntity;
import com.angeloni.nutricare.entity.CircumferenceEntity;
import com.angeloni.nutricare.entity.ClientEntity;
import com.angeloni.nutricare.entity.FoldEntity;
import com.angeloni.nutricare.repository.AnthropometryRepository;
import com.angeloni.nutricare.repository.CircumferenceRepository;
import com.angeloni.nutricare.repository.ClientRepository;
import com.angeloni.nutricare.repository.FoldRepository;

@Service
public class AnthropometryServiceImpl implements AnthropometryService {

	@Autowired private AnthropometryRepository anthropometryRepository;
	@Autowired private FoldRepository foldRepository;
	@Autowired private CircumferenceRepository circumferenceRepository;
	@Autowired private ClientRepository clientRepository;
	@Autowired private ModelMapper modelMapper;

	@Override
	@Transactional
	public AnthropometryDto saveVisit(Long clientId, AnthropometryDto dto) {
		ClientEntity client = clientRepository.findById(clientId)
				.orElseThrow(() -> new RuntimeException("Cliente non trovato: " + clientId));

		AnthropometryEntity antro = AnthropometryEntity.builder()
				.client(client).height(dto.getHeight()).weight(dto.getWeight()).build();
		antro = anthropometryRepository.save(antro);

		if (dto.getFold() != null && allFoldFieldsPresent(dto.getFold())) {
			FoldEntity fold = FoldEntity.builder()
					.anthropometry(antro)
					.pectoral(dto.getFold().getPectoral())
					.axillary(dto.getFold().getAxillary())
					.suprailiac(dto.getFold().getSuprailiac())
					.abdominal(dto.getFold().getAbdominal())
					.triceps(dto.getFold().getTriceps())
					.subscapolaris(dto.getFold().getSubscapolaris())
					.thigh(dto.getFold().getThigh())
					.build();
			foldRepository.save(fold);
		}

		if (dto.getCircumference() != null && allCircumferenceFieldsPresent(dto.getCircumference())) {
			CircumferenceEntity circ = CircumferenceEntity.builder()
					.anthropometry(antro)
					.chest(dto.getCircumference().getChest())
					.arm(dto.getCircumference().getArm())
					.waist(dto.getCircumference().getWaist())
					.hip(dto.getCircumference().getHip())
					.thigh(dto.getCircumference().getThigh())
					.build();
			circumferenceRepository.save(circ);
		}

		antro = anthropometryRepository.findById(antro.getId()).orElseThrow();
		return toDto(antro);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AnthropometryDto> getVisitsByClient(Long clientId) {
		return anthropometryRepository.findByClientIdOrderByCreatedAtDesc(clientId)
				.stream().map(this::toDto).collect(Collectors.toList());
	}

	private boolean allFoldFieldsPresent(FoldDto f) {
		return f.getPectoral() != null && f.getAxillary() != null
				&& f.getSuprailiac() != null && f.getAbdominal() != null
				&& f.getTriceps() != null && f.getSubscapolaris() != null
				&& f.getThigh() != null;
	}

	private boolean allCircumferenceFieldsPresent(CircumferenceDto c) {
		return c.getChest() != null && c.getArm() != null
				&& c.getWaist() != null && c.getHip() != null && c.getThigh() != null;
	}

	private AnthropometryDto toDto(AnthropometryEntity e) {
		AnthropometryDto dto = new AnthropometryDto();
		dto.setId(e.getId());
		dto.setHeight(e.getHeight());
		dto.setWeight(e.getWeight());
		dto.setCreatedAt(e.getCreatedAt());
		if (e.getFold() != null) {
			dto.setFold(modelMapper.map(e.getFold(), FoldDto.class));
		}
		if (e.getCircumference() != null) {
			dto.setCircumference(modelMapper.map(e.getCircumference(), CircumferenceDto.class));
		}
		return dto;
	}
}
