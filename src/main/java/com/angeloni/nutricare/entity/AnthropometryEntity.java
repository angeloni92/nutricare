package com.angeloni.nutricare.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "anthropometries")
@Data
public class AnthropometryEntity implements Serializable {
	
	private static final long serialVersionUID = 7790998269323022298L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false) 
    @JoinColumn(name = "client_id", nullable = false) 
	private ClientEntity client;

	@Column(name = "height", nullable = false)
	private Integer height;

	@Column(name = "weight", nullable = false)
	private Double weight;
	
	@OneToOne(mappedBy = "anthropometry", cascade = CascadeType.ALL, orphanRemoval = true) 
    private FoldEntity fold;
	
	@OneToOne(mappedBy = "anthropometry", cascade = CascadeType.ALL, orphanRemoval = true) 
    private CircumferenceEntity circumference;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
