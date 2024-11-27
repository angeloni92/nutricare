package com.angeloni.nutricare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.FoldEntity;

@Repository
public interface FoldRepository extends JpaRepository<FoldEntity, Long>{
}
