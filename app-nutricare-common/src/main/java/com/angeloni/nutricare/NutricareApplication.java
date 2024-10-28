package com.angeloni.nutricare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.angeloni.nutricare")
public class NutricareApplication {

	public static void main(String[] args) {
		SpringApplication.run(NutricareApplication.class, args);
	}

}
