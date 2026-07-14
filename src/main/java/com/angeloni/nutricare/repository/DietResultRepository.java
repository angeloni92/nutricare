package com.angeloni.nutricare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.DietResultEntity;
import com.angeloni.nutricare.entity.UserEntity;

@Repository
public interface DietResultRepository extends JpaRepository<DietResultEntity, Long> {

	Optional<DietResultEntity> findById(Long id);

	List<DietResultEntity> findByUser(UserEntity user);

	List<DietResultEntity> findByUserAndClientId(UserEntity user, Long clientId);
}

