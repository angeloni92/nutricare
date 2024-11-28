package com.angeloni.nutricare.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "clients")
@Data
public class ClientEntity implements Serializable {
	
	private static final long serialVersionUID = 6679356562435797710L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "user_id", nullable = false) 
	private UserEntity user;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "surname", nullable = false, length = 255)
	private String surname;
	
	@Column(name = "age", nullable = false)
	private Integer age;

	@Column(name = "country", nullable = false, length = 255)
	private String country;
	
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietDetailEntity> dietDetails;
	
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnthropometryEntity> anthropometries;
	
	@ElementCollection
    @CollectionTable(name = "client_allergies", 
                     joinColumns = @JoinColumn(name = "client_id")) 
    @Column(name = "allergy_name") 
    private List<String> allergies;
	
	@ElementCollection
    @CollectionTable(name = "client_health_conditions", 
                     joinColumns = @JoinColumn(name = "client_id")) 
    @Column(name = "health_condition") 
    private List<String> healthConditions;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
