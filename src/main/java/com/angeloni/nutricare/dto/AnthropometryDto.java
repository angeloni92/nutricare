package com.angeloni.nutricare.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AnthropometryDto {

	private Long id;

	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double height;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double weight;
	private FoldDto fold;
	private CircumferenceDto circumference;
	private LocalDateTime createdAt;

}
