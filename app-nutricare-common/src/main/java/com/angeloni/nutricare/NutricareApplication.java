package com.angeloni.nutricare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.angeloni.nutricare.*")
@EnableJpaRepositories(basePackages = "com.angeloni.nutricare.repository")
@EntityScan({ "com.angeloni.nutricare.entity" })
public class NutricareApplication {

	public static void main(String[] args) {
		SpringApplication.run(NutricareApplication.class, args);
	}

}
