package com.angeloni.nutricare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angeloni.nutricare.entity.ClientEntity;
import com.angeloni.nutricare.entity.UserEntity;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
	
	Optional<ClientEntity> findByNameAndSurnameAndUser(String name, String Surname, UserEntity user);
	
	List<ClientEntity> findByUser(UserEntity user);
}
