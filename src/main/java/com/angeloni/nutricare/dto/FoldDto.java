package com.angeloni.nutricare.dto;

import java.io.Serializable;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class FoldDto implements Serializable {
	
	private static final long serialVersionUID = 452739800217821387L;
	
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Integer pectoral;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Integer axillary;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Integer suprailiac;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Integer abdominal;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Integer triceps;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Integer subscapolaris;
	@PositiveOrZero(message = "Value must be higher or equal to zero")
	private Integer thigh;

}
