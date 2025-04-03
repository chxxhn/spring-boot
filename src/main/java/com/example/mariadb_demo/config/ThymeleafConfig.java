package com.example.mariadb_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafConfig {

    @Bean
    public MaskingUtil maskingUtil() {
        return new MaskingUtil();
    }
}

