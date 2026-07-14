package com.angeloni.nutricare.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class AiUserDto implements Serializable {

	private static final long serialVersionUID = -972859904699368616L;

	private Long id;

	private Long userId;

	private AiDto ai;

	private String aiKey;

}
