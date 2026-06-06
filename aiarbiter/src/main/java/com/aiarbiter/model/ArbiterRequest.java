package com.aiarbiter.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArbiterRequest(
        @NotBlank @Size(max = 10_000) String task,
        String workerA,
        String workerB,
        String judge
) {}
