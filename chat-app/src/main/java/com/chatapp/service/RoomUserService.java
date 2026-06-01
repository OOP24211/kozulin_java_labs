package com.chatapp.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomUserService {

    private final Map<String, Set<String>> roomUsers = new ConcurrentHashMap<>();
    private final Map<String, String[]> sessionInfo = new ConcurrentHashMap<>();

    public void addUser(String sessionId, String room, String username) {
        if (sessionId == null || room == null || username == null) return;
        roomUsers.computeIfAbsent(room, k -> ConcurrentHashMap.newKeySet()).add(username);
        sessionInfo.put(sessionId, new String[]{room, username});
    }

    public void removeUser(String sessionId) {
        if (sessionId == null) return;
        String[] info = sessionInfo.remove(sessionId);
        if (info == null || info.length < 2) return;
        Set<String> users = roomUsers.get(info[0]);
        if (users != null) users.remove(info[1]);
    }

    public void removeUser(String room, String username) {
        if (room == null || username == null) return;
        Set<String> users = roomUsers.get(room);
        if (users != null) users.remove(username);
        sessionInfo.entrySet().removeIf(e -> {
            String[] v = e.getValue();
            return v != null && v.length >= 2 && room.equals(v[0]) && username.equals(v[1]);
        });
    }

    public String[] getSessionInfo(String sessionId) {
        if (sessionId == null) return null;
        return sessionInfo.get(sessionId);
    }

    public List<String> getUsers(String room) {
        if (room == null) return List.of();
        Set<String> users = roomUsers.get(room);
        if (users == null) return List.of();
        List<String> list = new ArrayList<>(users);
        Collections.sort(list);
        return list;
    }
}
