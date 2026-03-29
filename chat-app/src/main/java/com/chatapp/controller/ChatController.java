package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.room/{room}")
    public void sendToRoom(@DestinationVariable String room,
                           Message message) {
        Message saved = messageService.save(
                message.getSender(),
                message.getContent(),
                room
        );
        messagingTemplate.convertAndSend("/topic/room/" + room, saved);
    }
}