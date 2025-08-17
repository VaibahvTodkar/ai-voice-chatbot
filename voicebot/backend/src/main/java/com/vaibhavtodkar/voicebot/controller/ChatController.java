package com.vaibhavtodkar.voicebot.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhavtodkar.voicebot.dto.AudioRequestDto;
import com.vaibhavtodkar.voicebot.dto.ChatResponseDto;
import com.vaibhavtodkar.voicebot.dto.TextRequestDto;
import com.vaibhavtodkar.voicebot.service.ChatService;
import com.vaibhavtodkar.voicebot.service.VoiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {


    @Autowired
    private VoiceService voiceService;
    @Autowired
    private ChatService chatService;

   

    @PostMapping("/text-to-speech")
    public String textToSpeech(@RequestBody TextRequestDto request) {
        return voiceService.textToSpeechBase64(request.getText());
    }

    // Convert speech (Base64) to text
    @PostMapping("/speech-to-text")
    public String speechToText(@RequestBody AudioRequestDto request) throws IOException {
        byte[] audioBytes = Base64.getDecoder().decode(request.getBase64Audio());
        Files.write(Paths.get("received_audio.wav"), audioBytes);
        return voiceService.speechToText(audioBytes);
    }
    
    

    @PostMapping(value = "/chat", produces = "application/json; charset=UTF-8")
    public ChatResponseDto chat(@RequestBody AudioRequestDto request) throws Exception {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        System.out.println("Received chat request: " + request.getBase64Audio() + " with text: " + request.getText());
        return chatService.chat(request);

    }
}