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
@Table(name = "folds")
@Data
public class FoldEntity implements Serializable {

	private static final long serialVersionUID = -331778432498883033L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.EAGER, optional = false) 
	@JoinColumn(name = "anthropometry_id", nullable = false, unique = true) 
	private AnthropometryEntity anthropometry;

	@Column(name = "pectoral", nullable = false)
	private Double pectoral;
	
	@Column(name = "axillary", nullable = false)
	private Double axillary;
	
	@Column(name = "suprailiac", nullable = false)
	private Double suprailiac;
	
	@Column(name = "abdominal", nullable = false)
	private Double abdominal;
	
	@Column(name = "triceps", nullable = false)
	private Double triceps;
	
	@Column(name = "subscapolaris", nullable = false)
	private Double subscapolaris;
	
	@Column(name = "thigh", nullable = false)
	private Double thigh;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
