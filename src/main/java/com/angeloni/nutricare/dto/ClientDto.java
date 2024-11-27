package com.angeloni.nutricare.dto;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientDto implements Serializable {

	private static final long serialVersionUID = 2244417344498037719L;
	@NotBlank(message = "Name is required")
	@Size(min = 2, message = "name must be min 2 characters")
	private String name;
	@NotBlank(message = "Surname is required")
	@Size(min = 2, message = "Surname must be min 2 characters")
	private String surname;
	@PositiveOrZero(message = "Age must be higher or equal to zero")
	private String age;
	@NotEmpty
	private List<DietDetailDto> dietDetails;
	@NotEmpty
	private List<AnthropometryDto> anthropometries;

}
