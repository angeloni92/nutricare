package com.angeloni.nutricare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.AnthropometryEntity;

@Repository
public interface AnthropometryRepository extends JpaRepository<AnthropometryEntity, Long>{
}
