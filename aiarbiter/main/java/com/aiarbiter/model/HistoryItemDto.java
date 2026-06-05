package com.aiarbiter.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record HistoryItemDto(
        Long id,
        String q,
        String ts,
        String winner,
        WorkerDto a,
        WorkerDto b,
        String verdict,
        Integer latencyMs
) {
    public record WorkerDto(String name, String body) {}
}
