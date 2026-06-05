package com.aiarbiter.client;

public interface AiClient {
    String getName();
    String complete(String prompt);
    String completeWithSearch(String prompt);
    CompletionResult completeWithTokens(String prompt);

    record CompletionResult(String text, int tokens) {}
}
