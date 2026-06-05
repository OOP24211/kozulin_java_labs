package com.aiarbiter;

import com.aiarbiter.config.AiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AiProperties.class)
public class AiArbiterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiArbiterApplication.class, args);
    }
}
