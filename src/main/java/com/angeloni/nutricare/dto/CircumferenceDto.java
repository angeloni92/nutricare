package com.angeloni.nutricare.dto;

import java.io.Serializable;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CircumferenceDto implements Serializable {
	
	private static final long serialVersionUID = -5529811047318544540L;
	
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
