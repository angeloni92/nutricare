package com.angeloni.nutricare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.AnthropometryEntity;

@Repository
public interface AnthropometryRepository extends JpaRepository<AnthropometryEntity, Long> {

	@Query("SELECT a FROM AnthropometryEntity a WHERE a.client.id = :clientId ORDER BY a.createdAt DESC")
	List<AnthropometryEntity> findByClientIdOrderByCreatedAtDesc(@Param("clientId") Long clientId);

	@Query("SELECT a FROM AnthropometryEntity a WHERE a.client.id = :clientId ORDER BY a.createdAt ASC")
	List<AnthropometryEntity> findByClientIdOrderByCreatedAtAsc(@Param("clientId") Long clientId);
}
