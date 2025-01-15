package com.angeloni.nutricare.dto;

import java.io.Serializable;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AnthropometryDto implements Serializable {

	private static final long serialVersionUID = 4183994057140521879L;
	
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double height;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double weight;
	private FoldDto fold;
	private CircumferenceDto circumference;

}
