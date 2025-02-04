package com.angeloni.nutricare.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.service.AuthService;
import com.angeloni.nutricare.util.JwtAuthenticationFilter;
import com.angeloni.nutricare.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends OncePerRequestFilter {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtTokenUtil jwtTokenUtil;
	private final AuthService authService;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtTokenUtil jwtTokenUtil,
			AuthService authService) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.jwtTokenUtil = jwtTokenUtil;
		this.authService = authService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						authz -> authz.requestMatchers("/auth/register", "/auth/confirm", "/auth/login").permitAll()
								.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.httpBasic(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Refresh-Token"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = getJwtFromRequest(request);

		try {
			if (token != null) {
				if (jwtTokenUtil.isTokenExpired(token)) {
					String refreshToken = getRefreshTokenFromRequest(request);
					String username = jwtTokenUtil.extractUsername(refreshToken);
					if (refreshToken != null && jwtTokenUtil.validateToken(refreshToken, username)) {
						String newAccessToken = jwtTokenUtil.generateToken(username);

						response.setHeader("Authorization", "Bearer " + newAccessToken);
						request.setAttribute("Authorization", "Bearer " + newAccessToken);
					} else {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.getWriter().write("Refresh token is expired or invalid");
						return;
					}
				}
				Authentication authentication = getAuthentication();
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (ExpiredJwtException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Token expired. Please login again.");
			return;
		}

		filterChain.doFilter(request, response);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		return (bearerToken != null && bearerToken.startsWith("Bearer ")) ? bearerToken.substring(7) : null;
	}

	private String getRefreshTokenFromRequest(HttpServletRequest request) {
		return request.getHeader("X-Refresh-Token");
	}

	private Authentication getAuthentication() {
		UserEntity user = authService.getUserFromSecurityContext();
		return new UsernamePasswordAuthenticationToken(user, null, null);
	}
}
