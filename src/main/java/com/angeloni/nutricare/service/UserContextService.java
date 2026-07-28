package com.angeloni.nutricare.service;

import com.angeloni.nutricare.entity.UserEntity;

public interface UserContextService {

	UserEntity getCurrentUser();

	void setCurrentUser(UserEntity user);

	void logout();

}