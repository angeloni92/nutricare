package com.angeloni.nutricare.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diet_results", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "user_id", "client_id", "created_at" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietResultEntity implements Serializable {

	private static final long serialVersionUID = -5012040156298447391L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(name = "client_id", nullable = false)
	private Long clientId;

	@Column(name = "generated_diet", columnDefinition = "LONGTEXT", nullable = false)
	private String generatedDiet;

	@Column(name = "ai_model", length = 100)
	private String aiModel;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
}

