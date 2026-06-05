package com.aiarbiter.controller;

import com.aiarbiter.model.ArbiterRequest;
import com.aiarbiter.model.ArbiterResponse;
import com.aiarbiter.model.HistoryItemDto;
import com.aiarbiter.model.User;
import com.aiarbiter.service.ArbiterService;
import com.aiarbiter.service.ChatHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArbiterController {

    private final ArbiterService arbiterService;
    private final ChatHistoryService chatHistoryService;

    @PostMapping("/run")
    public ArbiterResponse run(@Valid @RequestBody ArbiterRequest request,
                               @AuthenticationPrincipal User user) {
        return arbiterService.arbitrateAndSave(request, user);
    }

    @DeleteMapping("/history/{id}")
    public void deleteHistory(@PathVariable Long id, @AuthenticationPrincipal User user) {
        chatHistoryService.delete(id, user);
    }

    @GetMapping("/history")
    public List<HistoryItemDto> history(@AuthenticationPrincipal User user,
                                        @RequestParam(defaultValue = "0") int page) {
        return chatHistoryService.getHistory(user, page).stream()
                .map(h -> new HistoryItemDto(
                        h.getId(),
                        h.getQuestion(),
                        h.getCreatedAt() != null ? h.getCreatedAt().toString() : null,
                        h.getWinner(),
                        new HistoryItemDto.WorkerDto(
                                h.getWorkerAName() != null ? h.getWorkerAName() : "",
                                h.getWorkerAResponse() != null ? h.getWorkerAResponse() : ""
                        ),
                        new HistoryItemDto.WorkerDto(
                                h.getWorkerBName() != null ? h.getWorkerBName() : "",
                                h.getWorkerBResponse() != null ? h.getWorkerBResponse() : ""
                        ),
                        h.getVerdict(),
                        h.getLatencyMs()
                ))
                .toList();
    }
}
