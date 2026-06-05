package com.aiarbiter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "worker_a_name", length = 100)
    private String workerAName;

    @Column(name = "worker_a_response", columnDefinition = "TEXT")
    private String workerAResponse;

    @Column(name = "worker_a_tokens")
    private Integer workerATokens;

    @Column(name = "worker_b_name", length = 100)
    private String workerBName;

    @Column(name = "worker_b_response", columnDefinition = "TEXT")
    private String workerBResponse;

    @Column(name = "worker_b_tokens")
    private Integer workerBTokens;

    @Column(columnDefinition = "TEXT")
    private String verdict;

    @Column(length = 1)
    private String winner;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
