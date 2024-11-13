package com.angeloni.nutricare.service;

import com.angeloni.nutricare.dto.LoginDto;
import com.angeloni.nutricare.dto.UserDto;
public interface UserService {

	public static final String USERNAME_ALREADY_EXISTS = "Username already exists!";
	public static final String EMAIL_ALREADY_EXISTS = "Email already exists!";
	public static final String REGISTRATION_SUCCESS = "Registration successful!";

    String registerUser(UserDto userDto);
    
    String loginUser(LoginDto loginDto);
}
