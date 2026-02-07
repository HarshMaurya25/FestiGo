package com.example.FestiGo.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.domain:}")
    private String appDomain;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> allowed = new ArrayList<>();
        // Common local dev origins
        allowed.add("http://localhost:3000");
        allowed.add("http://localhost:8080");
        allowed.add("http://127.0.0.1:5500");

        if (appDomain != null && !appDomain.isBlank()) {
            allowed.add(appDomain);
        }

        registry
                .addMapping("/api/**")
                .allowedOrigins(allowed.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
