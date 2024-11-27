package com.angeloni.nutricare.dto;

import java.io.Serializable;

import com.angeloni.nutricare.enums.UserRoleEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto implements Serializable {

	private static final long serialVersionUID = 8633740362929514110L;

	@NotBlank(message = "Username is required")
	@Size(min = 5, max = 50, message = "Username must be between 3 and 50 characters")
	private String username;

	@NotBlank(message = "Password is required")
	@Size(min = 10, message = "Password must be at least 6 characters long")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$", message = "Password must contain at least one uppercase letter, one number, and one special character")
	private String password;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	private String email;
	
	private UserRoleEnum role = UserRoleEnum.USER;

}
