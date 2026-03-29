package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class HistoryController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/{room}/history")
    public List<Message> getHistory(@PathVariable String room) {
        return messageService.getHistory(room);
    }
}