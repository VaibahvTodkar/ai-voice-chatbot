package com.vaibhavtodkar.voicebot.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.Set;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vaibhavtodkar.voicebot.dto.AudioRequestDto;
import com.vaibhavtodkar.voicebot.dto.ChatResponseDto;
import com.vaibhavtodkar.voicebot.service.ChatService;
import com.vaibhavtodkar.voicebot.service.VoiceService;

import lombok.RequiredArgsConstructor;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    @Autowired VoiceService voiceService;
    @Autowired OllamaClient ollamaClient;
    private final MaryInterface maryTTS;


    public ChatServiceImpl() throws Exception {
		maryTTS = new LocalMaryInterface();

        // List available voices
        Set<String> voices = maryTTS.getAvailableVoices();

        if (voices.isEmpty()) {
            throw new IllegalStateException("No MaryTTS voices found. Please add voice components to classpath.");
        }

        // Use default if available, otherwise pick the first
        String defaultVoice = "cmu-slt-hsmm";
        if (voices.contains(defaultVoice)) {
            maryTTS.setVoice(defaultVoice);
        } else {
            maryTTS.setVoice(voices.iterator().next()); // safe now
        }
    }
    
    @Override
    public String generateSpeechBase64(String text) {
        try {
            // Generate WAV file
            File tempFile = File.createTempFile("speech", ".wav");
            AudioSystem.write(maryTTS.generateAudio(text), AudioFileFormat.Type.WAVE, tempFile);

            // Convert file to Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AudioSystem.write(AudioSystem.getAudioInputStream(tempFile), AudioFileFormat.Type.WAVE, baos);
            byte[] audioBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(audioBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	 @Override
	    public ChatResponseDto chat(AudioRequestDto request) throws Exception {
	        if (request == null) {
	            throw new IllegalArgumentException("Request cannot be null");
	        }

	        String userText = null;

	        // Case 1: If base64Audio is provided
	        if (request.getBase64Audio() != null && !request.getBase64Audio().isEmpty()) {
	            String base64Audio = request.getBase64Audio().trim();
	            byte[] audioBytes;
	            try {
	                audioBytes = Base64.getDecoder().decode(base64Audio);
	            } catch (IllegalArgumentException e) {
	                throw new IllegalArgumentException("Invalid Base64 audio data", e);
	            }

	            // Convert speech to text
	            userText = voiceService.speechToText(audioBytes);
	        }
	        // Case 2: If text is provided
	        else if (request.getText() != null && !request.getText().isEmpty()) {
	            userText = request.getText().trim();
	        }
	        // If neither is provided, throw error
	        else {
	            throw new IllegalArgumentException("Either base64Audio or text must be provided");
	        }

	        // Call Ollama client
	        String botResponse = ollamaClient.generate(userText);

	        // Convert bot response to audio (Base64)
	        String audioResponse = voiceService.textToSpeechBase64(botResponse);

	        // Return DTO with text + audio
	        ChatResponseDto responseDto = new ChatResponseDto();
	        responseDto.setTextReqest(userText);
	        responseDto.setTextResponse(botResponse);
	        System.out.println(botResponse);
	        responseDto.setAudioBase64(audioResponse);
	        
	       
	        return responseDto;
	    }
    

    
}