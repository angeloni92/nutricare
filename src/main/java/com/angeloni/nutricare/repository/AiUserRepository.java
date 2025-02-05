package com.angeloni.nutricare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.UserEntity;

@Repository
public interface AiUserRepository extends JpaRepository<AiUserEntity, Long> {
	
	Optional<AiUserEntity> findByAiKey(String aiKey);
	
	Optional<AiUserEntity> findByUser(UserEntity user);
	
}
