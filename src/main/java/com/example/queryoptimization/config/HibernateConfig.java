package com.example.queryoptimization.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return (properties) -> {
            properties.put("hibernate.order_inserts", "true");
            properties.put("hibernate.order_updates", "true");
            properties.put("hibernate.jdbc.batch_size", "500");
            properties.put("hibernate.jdbc.batch_versioned_data", "true");
            properties.put("hibernate.generate_statistics", "true");
        };
    }
}
