package com.angeloni.nutricare.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.angeloni.nutricare.enums.UserRoleEnum;

import lombok.Data;

@Data
public class UserDto {

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	private String username;

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters long")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$", message = "Password must contain at least one uppercase letter, one number, and one special character")
	private String password;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	private String email;
	
	private UserRoleEnum role = UserRoleEnum.USER;

}
