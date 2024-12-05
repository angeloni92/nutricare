package com.angeloni.nutricare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.AiEntity;
import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;

@Repository
public interface AiRepository extends JpaRepository<AiEntity, Long>{
	
	Optional<AiEntity> findByNameAndModel(AINameEnum name, AIModelEnum model);
}
