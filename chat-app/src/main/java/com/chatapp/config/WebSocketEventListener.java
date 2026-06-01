package com.chatapp.config;

import com.chatapp.service.RoomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private RoomUserService roomUserService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String[] info = roomUserService.getSessionInfo(sessionId);
        if (info == null) return;
        String room = info[0];
        roomUserService.removeUser(sessionId);
        messagingTemplate.convertAndSend("/topic/room/" + room + "/users",
                roomUserService.getUsers(room));
    }
}
