package com.angeloni.nutricare.service;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.exception.AuthException;
import com.angeloni.nutricare.exception.NotFoundException;
import com.angeloni.nutricare.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserEntity getUserFromSecurityContext() {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	String username = principal.toString();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format(DietService.USER_NOT_FOUND_FORMAT, username)));
        return user;
    }
	
	@Override
	public UserEntity retrieveUserFromAuthentication() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated()) {
				UserEntity user = (UserEntity) authentication.getPrincipal();

				return user;
			} else {
				throw new AuthenticationException();
			}
		} catch (AuthenticationException e) {
			throw new AuthException(AuthService.FAILED_AUTHENTICATION_MSG, e);
		}
	}

}
