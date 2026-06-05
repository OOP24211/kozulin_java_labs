package com.aiarbiter.model;

public record ArbiterResponse(
        String workerAName,
        String workerAResponse,
        Integer workerATokens,
        String workerBName,
        String workerBResponse,
        Integer workerBTokens,
        String verdict
) {}
