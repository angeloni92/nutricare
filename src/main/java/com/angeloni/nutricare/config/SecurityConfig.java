package com.angeloni.nutricare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.angeloni.nutricare.util.JwtAutenthicationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        // Disable CSRF for REST APIs (not necessary in this context)
	        .csrf(csrf -> csrf.disable())  

	        // Configure request authorization
	        .authorizeRequests(authz -> authz
	            // The registration and login routes are open
	            .requestMatchers("/auth/register", "/auth/login").permitAll()  
	            // All other routes require authentication
	            .anyRequest().authenticated()  
	            .and()
	            // Add the JWT authentication filter before the UsernamePasswordAuthenticationFilter
	            .addFilterBefore(new JwtAutenthicationFilter(), UsernamePasswordAuthenticationFilter.class)
	        )
	        
	        // Configure form-based login
	        .formLogin(form -> form
	            // URL for the custom login page (can be an endpoint returning a form or JSON)
	            .loginPage("/login")  
	            // Allow everyone access to the login page
	            .permitAll()  
	        )
	        
	        // Enable HTTP Basic authentication
	        .httpBasic(Customizer.withDefaults());  

	    return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder(); // BCrypt for secure password management
	}

 
}
