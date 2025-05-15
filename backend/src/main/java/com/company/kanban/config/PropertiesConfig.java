package com.company.kanban.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kanban")
public record PropertiesConfig(String dbUser, String dbPassword, String jwtSecret) {}
