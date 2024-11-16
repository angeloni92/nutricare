package com.angeloni.nutricare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.angeloni.nutricare.util.JwtAutenthicationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disabilita CSRF per le API REST (non necessario in questo contesto)
            
            // Configurazione delle autorizzazioni per le richieste
            .authorizeRequests(authz -> authz
                .antMatchers("/api/auth/register", "/api/auth/login").permitAll()  // Le rotte di registrazione e login sono aperte
                .anyRequest().authenticated()  // Tutte le altre rotte richiedono autenticazione
                .and()
                .addFilterBefore(new JwtAutenthicationFilter(), UsernamePasswordAuthenticationFilter.class)
            )
            
            // Configura il login tramite form
            .formLogin(form -> form
                .loginPage("/login")  // URL della pagina di login personalizzata (può essere un endpoint che restituisce un form o un JSON)
                .permitAll()  // Consente l'accesso a tutti alla pagina di login
            )
            
            // Abilita l'autenticazione HTTP Basic
            .httpBasic();  // Usa HTTP Basic

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt for secure password management
    }
 
}
