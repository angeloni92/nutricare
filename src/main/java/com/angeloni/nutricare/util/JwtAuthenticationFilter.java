package com.angeloni.nutricare.util;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Ottieni il token dall'header Authorization
            String token = request.getHeader("Authorization");
            
            if (token == null || !token.startsWith("Bearer ")) {
                logger.warn("Richiesta senza token o token non valido");
                filterChain.doFilter(request, response);
                return;
            }

            // Rimuovi il prefisso "Bearer " e spazi indesiderati
            token = token.replace("Bearer ", "").trim(); 

            // Verifica formato del token (deve avere 3 parti separate da ".")
            if (token.split("\\.").length != 3) {
                logger.error("Token JWT malformato: " + token);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token JWT malformato");
                return;
            }

            String username = jwtTokenUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Valida il token utilizzando la chiave segreta
                if (jwtTokenUtil.validateToken(token, username)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Errore durante la decodifica del token JWT: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore del server");
            return;
        }

        filterChain.doFilter(request, response);
    }
}