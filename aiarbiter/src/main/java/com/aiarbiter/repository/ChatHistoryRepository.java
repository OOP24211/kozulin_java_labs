package com.aiarbiter.repository;

import com.aiarbiter.model.ChatHistory;
import com.aiarbiter.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    Page<ChatHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
