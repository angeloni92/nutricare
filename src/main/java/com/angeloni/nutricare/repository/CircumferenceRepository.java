package com.angeloni.nutricare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.CircumferenceEntity;

@Repository
public interface CircumferenceRepository extends JpaRepository<CircumferenceEntity, Long>{
}
