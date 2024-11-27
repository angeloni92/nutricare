package com.angeloni.nutricare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.AiEntity;

@Repository
public interface AiRepository extends JpaRepository<AiEntity, Long>{
}
