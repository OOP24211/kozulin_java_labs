package com.aiarbiter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
public record AiProperties(
        Providers providers,
        String allowedModels
) {
    public record Providers(
            Provider workerA,
            Provider workerB,
            Provider judge
    ) {}

    public record Provider(
            String baseUrl,
            String apiKey,
            String model
    ) {}
}
