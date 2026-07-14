package com.angeloni.nutricare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for serving the Angular frontend as a Single Page Application (SPA).
 * This ensures that all requests to undefined routes are forwarded to index.html,
 * allowing Angular Router to handle the routing on the client side.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configure resource handlers for static resources.
     * Ensures that CSS, JS, and other static assets are served correctly.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static resources from classpath
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
        
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.ico");
    }

    /**
     * Configure view controllers for SPA routing.
     * Forwards requests to non-existent paths to index.html so Angular Router can handle them.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward all requests that don't match static resources to index.html
        // This allows Angular Router to handle the routing
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}

