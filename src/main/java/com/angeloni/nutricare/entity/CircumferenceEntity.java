package com.angeloni.nutricare.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "circumferences")
@Data
public class CircumferenceEntity implements Serializable {

	private static final long serialVersionUID = 5556701903818380119L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.EAGER, optional = false) 
	@JoinColumn(name = "anthropometry_id", nullable = false, unique = true) 
	private AnthropometryEntity anthropometry;

	@Column(name = "chest", nullable = false)
	private Double chest;
	
	@Column(name = "arm", nullable = false)
	private Double arm;
	
	@Column(name = "waist", nullable = false)
	private Double waist;
	
	@Column(name = "hip", nullable = false)
	private Double hip;
	
	@Column(name = "thigh", nullable = false)
	private Double thigh;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
