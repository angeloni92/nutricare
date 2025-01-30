package com.angeloni.nutricare.service;

import com.angeloni.nutricare.entity.UserEntity;

public interface AuthService {
	
	public static final String FAILED_AUTHENTICATION_MSG = "Authentication failed";
	
	UserEntity getUserFromSecurityContext();
	
	UserEntity retrieveUserFromAuthentication();

}
