package com.angeloni.nutricare.dto;

import java.io.Serializable;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class FoldDto implements Serializable {
	
	private static final long serialVersionUID = 452739800217821387L;
	
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double pectoral;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double axillary;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double suprailiac;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double abdominal;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double triceps;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double subscapolaris;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Double thigh;

}
