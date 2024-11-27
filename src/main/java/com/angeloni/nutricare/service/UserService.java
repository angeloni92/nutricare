package com.angeloni.nutricare.service;

import com.angeloni.nutricare.dto.LoginDto;
import com.angeloni.nutricare.dto.UserDto;
public interface UserService {

	public static final String BEARER = "Bearer ";
	public static final String USERNAME_ALREADY_EXISTS = "Username already exists!";
	public static final String EMAIL_ALREADY_EXISTS = "Email already exists!";
	public static final String REGISTRATION_SUCCESS = "Registration successful!";
	public static final String INVALID_CREDENTIAL_ERROR_MESSAGE = "Invalid username or password.";
	public static final String CONFIRM_REGISTRATION = "Please, check your email to confirm registration";
	public static final String EMAIL_CONFIRM_REGISTRATION_SUBJECT = "Confirm your registration on Nutricare";
	public static String CONFIRMATION_LINK_FORMAT = "%s:%s%s/confirm?token=%s";
	public static String CONFIRM_REGISTRATION_EMAIL_FORMAT = "<h1>Welcome to Nutricare!</h1><p>To complete your registration, click on the following link:</p>" +
			"<a href=\"%s\">Confirm your email</a>";
	public static final String ERROR_SENDING_EMAIL_MSG = "Error sending email";
	public static final String INVALID_CONFERMATION_TOKEN = "Invalid confermation token.";
	public static final String EMAIL_IS_ALREADY_CONFIRMED = "Email is already confirmed.";
	public static final String EMAIL_SUCCESSFULLY_CONFIRMED = "Email successfully confirmed!";
	
    String registerUser(UserDto userDto);
    
    String loginUser(LoginDto loginDto);
    
    String confirmEmail(String token);
}
