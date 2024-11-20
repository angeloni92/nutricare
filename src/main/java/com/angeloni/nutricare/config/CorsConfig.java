package com.angeloni.nutricare.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Value("${cors.allowed.origin}")
	private String[] corsAllowedOrigin;
	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configura le rotte con CORS
        registry.addMapping("/**")  // Aggiungi la rotta /api o altre rotte necessarie
            .allowedOrigins(corsAllowedOrigin)  // Il dominio del tuo frontend (Angular)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // I metodi consentiti
            .allowedHeaders("*")  // Gli header che vuoi consentire
            .allowCredentials(true)  // Permette i cookie se necessari
        	.maxAge(3600);
    }
}
