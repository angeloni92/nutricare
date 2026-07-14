package com.angeloni.nutricare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.CopilotConnectionEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.OAuthProviderEnum;

@Repository
public interface CopilotConnectionRepository extends JpaRepository<CopilotConnectionEntity, Long> {

	Optional<CopilotConnectionEntity> findByUserAndProvider(UserEntity user, OAuthProviderEnum provider);

	void deleteByUserAndProvider(UserEntity user, OAuthProviderEnum provider);
}

