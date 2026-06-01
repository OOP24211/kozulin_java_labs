package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.service.MessageService;
import com.chatapp.service.RoomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RoomUserService roomUserService;

    @MessageMapping("/chat.room/{room}")
    public void sendToRoom(@DestinationVariable String room, Message message) {
        if (message == null || message.getSender() == null || message.getContent() == null) return;
        if (room == null || room.isBlank()) return;
        try {
            Message saved = messageService.save(message.getSender(), message.getContent(), room);
            messagingTemplate.convertAndSend("/topic/room/" + room, saved);
        } catch (Exception e) {
            // Не прерываем работу сервера при ошибке одного сообщения
        }
    }

    @MessageMapping("/chat.join/{room}")
    public void joinRoom(@DestinationVariable String room,
                         @Payload Map<String, String> payload,
                         SimpMessageHeaderAccessor headerAccessor) {
        if (room == null || room.isBlank() || payload == null) return;
        String sessionId = headerAccessor.getSessionId();
        String username = payload.get("sender");
        if (sessionId == null || username == null || username.isBlank()) return;
        roomUserService.addUser(sessionId, room, username.trim());
        broadcastUsers(room);
    }

    @MessageMapping("/chat.leave/{room}")
    public void leaveRoom(@DestinationVariable String room,
                          @Payload Map<String, String> payload) {
        if (room == null || room.isBlank() || payload == null) return;
        String username = payload.get("sender");
        if (username == null || username.isBlank()) return;
        roomUserService.removeUser(room, username.trim());
        broadcastUsers(room);
    }

    private void broadcastUsers(String room) {
        try {
            List<String> users = roomUserService.getUsers(room);
            messagingTemplate.convertAndSend("/topic/room/" + room + "/users", users);
        } catch (Exception e) {
            // Не прерываем работу при ошибке рассылки
        }
    }
}
