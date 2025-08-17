package com.vaibhavtodkar.voicebot.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class MediaService {

    @Value("${app.media.base-path}")
    private String basePath;

    public Resource loadAudio(String id) {
        Path p = Path.of(basePath, id + ".mp3");
        return new FileSystemResource(p.toFile());
    }

    public boolean audioExists(String id) {
        return Files.exists(Path.of(basePath, id + ".mp3"));
    }

    public String saveMockAudio() {
        // Placeholder: create empty file to simulate media presence
        try {
            Files.createDirectories(Path.of(basePath));
            String id = "aud_" + System.currentTimeMillis();
            Path p = Path.of(basePath, id + ".mp3");
            if (!Files.exists(p)) Files.write(p, new byte[0]);
            return id;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}