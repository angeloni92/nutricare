package com.angeloni.nutricare.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientDto {

	private Long id;
	@NotBlank(message = "Name is required")
	@Size(min = 2, message = "name must be min 2 characters")
	private String name;
	@NotBlank(message = "Surname is required")
	@Size(min = 2, message = "Surname must be min 2 characters")
	private String surname;
	@PositiveOrZero(message = "Age must be higher or equal to zero")
	private Integer age;
	@NotBlank(message = "country is required")
	@Size(min = 2, message = "Country must be min 2 characters")
	private String country;
	private List<String> allergies;
    private List<String> healthConditions; 

}
