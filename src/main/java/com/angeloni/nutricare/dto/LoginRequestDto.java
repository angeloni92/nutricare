package com.angeloni.nutricare.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto implements Serializable {
	
	private static final long serialVersionUID = 6494620920313781968L;

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	private String username;

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters long")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$", message = "Password must contain at least one uppercase letter, one number, and one special character")
	private String password;

}
