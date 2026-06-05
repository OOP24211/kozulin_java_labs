package com.aiarbiter.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class OpenAiCompatibleClient implements AiClient {

    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 120_000;

    private final RestClient restClient;
    private final String model;
    private final String name;

    public OpenAiCompatibleClient(String name, ProviderConfig config) {
        this.name = name;
        this.model = config.model();

        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(READ_TIMEOUT_MS);

        this.restClient = RestClient.builder()
                .baseUrl(config.baseUrl())
                .defaultHeader("Authorization", "Bearer " + config.apiKey())
                .defaultHeader("Content-Type", "application/json")
                .requestFactory(factory)
                .build();
    }

    @Override
    public String getName() { return name; }

    @Override
    public String complete(String prompt) {
        return completeWithTokens(prompt).text();
    }

    @Override
    public String completeWithSearch(String prompt) {
        var searchTool = Map.of(
                "type", "function",
                "function", Map.of(
                        "name", "web_search",
                        "description", "Search the web for current information",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of("query", Map.of("type", "string")),
                                "required", List.of("query")
                        )
                )
        );
        var body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "tools", List.of(searchTool),
                "tool_choice", "auto"
        );
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> json = restClient.post()
                    .uri("/chat/completions").body(body).retrieve().body(Map.class);
            var choices = (List<Map<String, Object>>) json.get("choices");
            if (choices == null || choices.isEmpty()) return "";
            var choice = choices.get(0);
            String finishReason = (String) choice.get("finish_reason");
            var message = (Map<String, Object>) choice.get("message");
            if (message == null) return "";
            if ("tool_calls".equals(finishReason) && message.containsKey("tool_calls")) {
                var toolCalls = (List<Map<String, Object>>) message.get("tool_calls");
                if (toolCalls == null || toolCalls.isEmpty()) return (String) message.getOrDefault("content", "");
                String callId = (String) toolCalls.get(0).get("id");
                var messages = List.of(
                        Map.of("role", "user", "content", prompt),
                        Map.of("role", "assistant", "content", "", "tool_calls", message.get("tool_calls")),
                        Map.of("role", "tool", "tool_call_id", callId,
                                "content", "Web search result not available. Use your knowledge.")
                );
                @SuppressWarnings("unchecked")
                Map<String, Object> r = restClient.post()
                        .uri("/chat/completions")
                        .body(Map.of("model", model, "messages", messages))
                        .retrieve().body(Map.class);
                var c = (List<Map<String, Object>>) r.get("choices");
                if (c == null || c.isEmpty()) return "";
                var m = (Map<String, Object>) c.get(0).get("message");
                return m != null ? (String) m.getOrDefault("content", "") : "";
            }
            return (String) message.getOrDefault("content", "");
        } catch (Exception e) {
            log.warn("tool calling failed ({}), falling back to plain complete", e.getMessage());
            return complete(prompt);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public AiClient.CompletionResult completeWithTokens(String prompt) {
        var request = new ChatRequest(model, List.of(new Message("user", prompt)));
        Map<String, Object> json = restClient.post()
                .uri("/chat/completions").body(request).retrieve().body(Map.class);
        var choices = (List<Map<String, Object>>) json.get("choices");
        if (choices == null || choices.isEmpty()) return new AiClient.CompletionResult("", 0);
        var message = (Map<String, Object>) choices.get(0).get("message");
        String text = message != null ? (String) message.getOrDefault("content", "") : "";
        var usage = (Map<String, Object>) json.get("usage");
        int tokens = usage != null ? ((Number) usage.getOrDefault("completion_tokens", 0)).intValue() : 0;
        return new AiClient.CompletionResult(text, tokens);
    }

    private record ChatRequest(String model, List<Message> messages) {}
    private record Message(String role, String content) {}
}
