package com.angeloni.nutricare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving the Single Page Application (SPA).
 * This controller ensures that all non-API requests are forwarded to the Angular frontend.
 */
@Controller
public class SpaController {

    /**
     * Forwards all non-API routes to the Angular index.html file.
     * This allows the Angular Router to handle client-side routing.
     * 
     * Excluded paths:
     * - /api/* - API endpoints are handled by their respective controllers
     * - /swagger-ui* - Swagger/OpenAPI documentation
     * - /v3/api-docs* - OpenAPI spec endpoints
     * - /actuator* - Spring Boot Actuator endpoints
     * - /static/* - Static resources
     */
    @GetMapping(value = {
        "/", 
        "/{x:[\\w\\-]+}", 
        "/{x:^(?!api|swagger-ui|v3|actuator).*$}/**/{y:[\\w\\-]+}"
    })
    public String spa() {
        return "forward:/index.html";
    }
}

