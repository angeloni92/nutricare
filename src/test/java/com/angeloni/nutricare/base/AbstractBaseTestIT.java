package com.angeloni.nutricare.base;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.google.gson.Gson;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@TestPropertySource(properties = { "sprinc.config.location = classpath:application_test.properties" })
public abstract class AbstractBaseTestIT {
	
	@Autowired
	protected ModelMapper modelMapper;
	
	@Autowired
	protected Gson gson;

}
