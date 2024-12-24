package com.angeloni.nutricare.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.angeloni.nutricare.util.JwtAutenthicationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        // Disable CSRF for REST APIs (not necessary in this context)
	    	.cors()
	    	.and()
	        .csrf(csrf -> csrf.disable())  

	        // Configure request authorization
	        .authorizeRequests(authz -> authz
	            // The registration and login routes are open
	            .requestMatchers("/auth/register", "/auth/confirm", "/auth/login").permitAll()  
	            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
	            // All other routes require authentication
	            .anyRequest().authenticated()  
	            .and()
	            // Add the JWT authentication filter before the UsernamePasswordAuthenticationFilter
	            .addFilterBefore(new JwtAutenthicationFilter(), UsernamePasswordAuthenticationFilter.class)
	        )
	        
	        // Enable HTTP Basic authentication
	        .httpBasic(Customizer.withDefaults());  

	    return http.build();
	}
	
	 @Bean
	    public CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration configuration = new CorsConfiguration();
	        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
	        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
	        configuration.setAllowCredentials(true);
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", configuration);
	        return source;
	    }

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder(); // BCrypt for secure password management
	}

 
}
