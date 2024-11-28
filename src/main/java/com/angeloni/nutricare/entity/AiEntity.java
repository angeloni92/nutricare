package com.angeloni.nutricare.entity;

import java.io.Serializable;

import com.angeloni.nutricare.enums.AINameEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ai")
@Data
public class AiEntity implements Serializable {
	
	private static final long serialVersionUID = 1919048970295170497L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "name", nullable = false, length = 255)
	private AINameEnum name;

	@Column(name = "model", nullable = false, length = 255)
	private String model;

}
