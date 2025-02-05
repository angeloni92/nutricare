package com.angeloni.nutricare.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CircumferenceDto {
	
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double chest;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double arm;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double waist;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double hip;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double thigh;
}
