package com.angeloni.nutricare.util;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAutenthicationFilter extends OncePerRequestFilter {
	
	@Autowired
	JwtTokenUtil jwtTokenUtil;
	
	/**
	 * Filters incoming HTTP requests to extract and validate the JWT token from the Authorization header.
	 * If the token is valid, it sets the authentication context for the current user.
	 *
	 * @param request  the {@link HttpServletRequest} containing the incoming request details
	 * @param response the {@link HttpServletResponse} used to send the response
	 * @param filterChain the {@link FilterChain} to pass the request and response to the next filter in the chain
	 * @throws {@link ServletException} if an error occurs during filtering
	 * @throws {@link IOException} if an I/O error occurs during filtering
	 *
	 * This method:
	 * - Extracts the JWT token from the "Authorization" header, expecting a "Bearer " prefix.
	 * - Validates the token using the {@link jwtTokenUtil}.
	 * - If the token is valid, it extracts the username and creates an {@link UsernamePasswordAuthenticationToken} with the username.
	 * - Sets the authentication context for the current user using {@link SecurityContextHolder}.
	 * - Passes the request and response to the next filter in the filter chain.
	 */
	  @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	            throws ServletException, IOException {
	        String token = request.getHeader("Authorization");
	        
	        if (token != null && token.startsWith("Bearer ")) {
	            token = token.substring(7);  
	            
	            String username = jwtTokenUtil.extractUsername(token);
	            if (username != null && jwtTokenUtil.validateToken(token, username)) {
	            	UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
	                SecurityContextHolder.getContext().setAuthentication(authentication);
	            }
	        }

	        filterChain.doFilter(request, response);
	    }

}
