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
