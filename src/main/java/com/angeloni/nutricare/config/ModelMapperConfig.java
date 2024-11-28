package com.angeloni.nutricare.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		// Optional configuration to improve mapping behavior
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT) // Enforces strict matching of
																						// field names
				.setFieldMatchingEnabled(true) // Enables matching of fields even without setters
				.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE); // Allows access to
																								// private fields
		return modelMapper;
	}
}
