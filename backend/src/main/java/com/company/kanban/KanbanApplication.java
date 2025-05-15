package com.company.kanban;

import com.company.kanban.config.PropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(PropertiesConfig.class)
public class KanbanApplication {
	public static void main(String[] args) {
		SpringApplication.run(KanbanApplication.class, args);
	}

	@Bean
	public PasswordEncoder bCryptEncoder() {
		return new BCryptPasswordEncoder();
	}

}
