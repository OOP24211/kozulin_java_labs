package com.chatapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService() {
        uploadDir = Paths.get("").toAbsolutePath().resolve("uploads");
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку uploads", e);
        }
    }

    public String store(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "file";
        }
        // Убираем path traversal: берём только имя файла без директорий
        String safeName = Paths.get(originalName).getFileName().toString()
                .replaceAll("[^a-zA-Z0-9._\\-а-яА-ЯёЁ]", "_");
        String filename = UUID.randomUUID() + "_" + safeName;
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, uploadDir.resolve(filename));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл: " + filename, e);
        }
        return "/uploads/" + filename;
    }
}
