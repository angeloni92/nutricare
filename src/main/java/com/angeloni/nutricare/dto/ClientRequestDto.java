package com.angeloni.nutricare.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientRequestDto implements Serializable {

	private static final long serialVersionUID = 2244417344498037719L;
	
	@NotNull
	private ClientDto client;
	@NotNull
	private DietDetailDto dietDetail;
	

}
