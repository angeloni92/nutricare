package com.angeloni.nutricare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
	
	Optional<UserEntity> findById(Long id);
	
	Optional<UserEntity> findByUsername(String username);
	
	Optional<UserEntity> findByEmail(String email);
	
	Optional<UserEntity> findByUsernameOrEmailAndEmailConfirmedTrue(String username, String email);
	
	Optional<UserEntity> findByConfirmationToken(String confirmationToken);
}
