package com.angeloni.nutricare.util;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;



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
