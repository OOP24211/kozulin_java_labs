package com.chatapp.controller;

import com.chatapp.model.Message;
import com.chatapp.service.FileStorageService;
import com.chatapp.service.MessageService;
import com.chatapp.service.RoomService;
import com.chatapp.service.RoomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class HistoryController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private RoomUserService roomUserService;

    @Autowired
    private RoomService roomService;

    @GetMapping
    public List<String> getRooms() {
        return roomService.getAllRooms();
    }

    @PostMapping
    public ResponseEntity<Void> createRoom(@RequestBody Map<String, String> body) {
        if (body == null || body.get("name") == null || body.get("name").isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        roomService.createRoom(body.get("name"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{room}/users")
    public List<String> getUsers(@PathVariable String room) {
        return roomUserService.getUsers(room);
    }

    @GetMapping("/{room}/history")
    public List<Message> getHistory(@PathVariable String room) {
        return messageService.getHistory(room);
    }

    @PostMapping("/{room}/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @PathVariable String room,
            @RequestParam("file") MultipartFile file,
            @RequestParam("sender") String sender) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Файл пустой"));
        }
        if (sender == null || sender.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Не указан отправитель"));
        }
        try {
            String fileUrl = fileStorageService.store(file);
            String safeName = fileUrl.substring(fileUrl.lastIndexOf('_') + 1);
            String content = "[file:" + fileUrl + ":" + safeName + "]";
            messageService.save(sender, content, room);
            return ResponseEntity.ok(Map.of("url", fileUrl, "name", safeName));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Ошибка загрузки файла"));
        }
    }
}
