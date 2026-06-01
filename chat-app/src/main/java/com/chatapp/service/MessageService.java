package com.chatapp.service;

import com.chatapp.model.Message;
import com.chatapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository repository;

    public Message save(String sender, String content, String room) {
        if (sender == null || sender.isBlank()) sender = "Аноним";
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Пустое сообщение");
        if (room == null || room.isBlank()) throw new IllegalArgumentException("Комната не указана");
        Message message = new Message();
        message.setSender(sender.trim());
        message.setContent(content);
        message.setRoom(room.trim());
        message.setTimestamp(LocalDateTime.now());
        return repository.save(message);
    }

    public List<Message> getHistory(String room) {
        if (room == null || room.isBlank()) return List.of();
        Pageable pageable = PageRequest.of(0, 50);
        List<Message> messages = new ArrayList<>(repository
                .findByRoomOrderByTimestampDesc(room, pageable)
                .getContent());
        Collections.reverse(messages);
        return messages;
    }
}
