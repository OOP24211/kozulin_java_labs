package com.aiarbiter.service;

import com.aiarbiter.client.AiClient;
import com.aiarbiter.client.OpenAiCompatibleClient;
import com.aiarbiter.client.ProviderConfig;
import java.util.regex.Pattern;
import com.aiarbiter.config.AiProperties;
import com.aiarbiter.model.ArbiterRequest;
import com.aiarbiter.model.ArbiterResponse;
import com.aiarbiter.model.ChatHistory;
import com.aiarbiter.model.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbiterService {

    private final AiProperties ai;
    private final ChatHistoryService chatHistoryService;
    @Qualifier("arbiterExecutor")
    private final Executor executor;

    private Set<String> allowedModels;

    @PostConstruct
    void init() {
        String key = ai.providers().workerA().apiKey();
        if (key == null || key.isBlank() || key.startsWith("${")) {
            throw new IllegalStateException("ONLYSQ_API_KEY is not set");
        }
        allowedModels = Arrays.stream(ai.allowedModels().split(","))
                .map(String::strip)
                .collect(Collectors.toSet());
        log.info("ArbiterService ready, {} models allowed", allowedModels.size());
    }

    public ArbiterResponse arbitrate(ArbiterRequest request) {
        var p = ai.providers();
        String modelA     = resolve(request.workerA(), p.workerA().model());
        String modelB     = resolve(request.workerB(), p.workerB().model());
        String modelJudge = resolve(request.judge(),   p.judge().model());

        AiClient workerA = new OpenAiCompatibleClient(modelA, new ProviderConfig(p.workerA().baseUrl(), p.workerA().apiKey(), modelA));
        AiClient workerB = new OpenAiCompatibleClient(modelB, new ProviderConfig(p.workerB().baseUrl(), p.workerB().apiKey(), modelB));
        AiClient judge   = new OpenAiCompatibleClient(modelJudge, new ProviderConfig(p.judge().baseUrl(), p.judge().apiKey(), modelJudge));

        var futureA = CompletableFuture.supplyAsync(() -> {
            try { return workerA.completeWithTokens(request.task()); }
            catch (Exception e) {
                log.warn("Worker A failed: {}", e.getMessage());
                return new AiClient.CompletionResult("Ошибка: " + workerA.getName(), 0);
            }
        }, executor);
        var futureB = CompletableFuture.supplyAsync(() -> {
            try { return workerB.completeWithTokens(request.task()); }
            catch (Exception e) {
                log.warn("Worker B failed: {}", e.getMessage());
                return new AiClient.CompletionResult("Ошибка: " + workerB.getName(), 0);
            }
        }, executor);

        var resA = futureA.join();
        var resB = futureB.join();

        String judgePrompt = buildJudgePrompt(request.task(), resA.text(), resB.text());
        String verdict;
        try {
            verdict = judge.completeWithSearch(judgePrompt);
        } catch (Exception e) {
            log.warn("Judge failed: {}", e.getMessage());
            verdict = "Ошибка: судья не ответил";
        }

        return new ArbiterResponse(
                workerA.getName(), resA.text(), resA.tokens(),
                workerB.getName(), resB.text(), resB.tokens(),
                verdict
        );
    }

    public ArbiterResponse arbitrateAndSave(ArbiterRequest request, User user) {
        long t0 = System.currentTimeMillis();
        ArbiterResponse response = arbitrate(request);
        int latencyMs = (int) (System.currentTimeMillis() - t0);

        var history = new ChatHistory();
        history.setUser(user);
        history.setQuestion(request.task());
        history.setWorkerAName(response.workerAName());
        history.setWorkerAResponse(response.workerAResponse());
        history.setWorkerATokens(response.workerATokens());
        history.setWorkerBName(response.workerBName());
        history.setWorkerBResponse(response.workerBResponse());
        history.setWorkerBTokens(response.workerBTokens());
        history.setVerdict(response.verdict());
        history.setWinner(detectWinner(response.verdict()));
        history.setLatencyMs(latencyMs);
        chatHistoryService.save(history);

        return response;
    }

    private static final Pattern WINNER_A = Pattern.compile("(?si).*(победитель|winner)\\s*[:\\-]?\\s*a.*");
    private static final Pattern WINNER_B = Pattern.compile("(?si).*(победитель|winner)\\s*[:\\-]?\\s*b.*");

    private String detectWinner(String verdict) {
        if (verdict == null) return null;
        if (WINNER_A.matcher(verdict).matches()) return "A";
        if (WINNER_B.matcher(verdict).matches()) return "B";
        return null;
    }

    private String resolve(String requested, String fallback) {
        if (requested != null && !requested.isBlank() && allowedModels.contains(requested.strip())) {
            return requested.strip();
        }
        return fallback;
    }

    private String buildJudgePrompt(String task, String a, String b) {
        return """
                Ты — нейросеть-судья с доступом к веб-поиску.

                Задача: %s

                Ответ A: %s

                Ответ B: %s

                Инструкция:
                1. Если в ответах есть факты, которые могут быть устаревшими или сомнительными — используй web_search для проверки.
                2. Сравни ответы по критериям: точность, полнота, понятность, польза.
                3. Укажи если какой-то ответ содержал ошибки или устаревшую информацию.
                4. В конце ОБЯЗАТЕЛЬНО напиши ровно одну строку:
                Победитель: A
                или
                Победитель: B
                """.formatted(task, trimTo(a, 1500), trimTo(b, 1500));
    }

    private String trimTo(String text, int max) {
        return text != null && text.length() > max
                ? text.substring(0, max) + "... [обрезано]"
                : text;
    }
}
