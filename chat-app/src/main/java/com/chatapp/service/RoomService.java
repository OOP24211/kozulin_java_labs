package com.chatapp.service;

import com.chatapp.model.Room;
import com.chatapp.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<String> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(Room::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    @Transactional
    public void createRoom(String name) {
        if (name == null || name.isBlank()) return;
        String trimmed = name.trim();
        if (!roomRepository.existsByName(trimmed)) {
            roomRepository.save(new Room(trimmed));
        }
    }
}
