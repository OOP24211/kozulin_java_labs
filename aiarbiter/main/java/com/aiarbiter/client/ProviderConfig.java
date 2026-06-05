package com.aiarbiter.client;

public record ProviderConfig(
        String baseUrl,
        String apiKey,
        String model
) {}
