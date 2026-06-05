package com.aiarbiter.service;

import com.aiarbiter.model.ChatHistory;
import com.aiarbiter.model.User;
import com.aiarbiter.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private static final int PAGE_SIZE = 30;

    private final ChatHistoryRepository chatHistoryRepository;

    @Transactional
    public void save(ChatHistory history) {
        chatHistoryRepository.save(history);
    }

    public List<ChatHistory> getHistory(User user, int page) {
        return chatHistoryRepository
                .findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, PAGE_SIZE))
                .getContent();
    }

    @Transactional
    public void delete(Long id, User user) {
        chatHistoryRepository.findById(id)
                .filter(h -> h.getUser().getId().equals(user.getId()))
                .ifPresent(chatHistoryRepository::delete);
    }
}
