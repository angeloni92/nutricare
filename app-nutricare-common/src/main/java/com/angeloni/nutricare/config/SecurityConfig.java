package com.angeloni.nutricare.config;


import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disabilita CSRF (necessario per client JavaScript)
                // Configura le rotte e le autorizzazioni
                .authorizeRequests(authz -> authz
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()  // Le rotte di registrazione e login sono aperte
                        .anyRequest().authenticated()  // Tutte le altre rotte richiedono autenticazione
                )
                // Configura l'autenticazione HTTP di base senza usare .httpBasic() (gestito tramite il filtro)
                .httpBasic(Customizer.withDefaults());  // Usa l'autenticazione di base (Basic Authentication)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Usato per criptare la password
    }
}
