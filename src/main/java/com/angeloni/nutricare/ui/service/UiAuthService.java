package com.angeloni.nutricare.ui.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.angeloni.nutricare.dto.LoginRequestDto;

/**
 * UI Service for authentication operations
 * Communicates with the backend authentication API
 */
@Service
public class UiAuthService {

    private final RestTemplate restTemplate;
    private static final String API_BASE_URL = "http://localhost:8080/api/nutricare";
    private String authToken;

    @Autowired
    public UiAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String login(String username, String password) throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        try {
            var response = restTemplate.postForObject(
                API_BASE_URL + "/auth/login",
                loginRequest,
                java.util.Map.class
            );

            if (response != null && response.containsKey("token")) {
                authToken = (String) response.get("token");
                return authToken;
            }
            return null;
        } catch (Exception e) {
            throw new Exception("Login failed: " + e.getMessage());
        }
    }

    public void logout() {
        authToken = null;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }
}

