package com.vaibhavtodkar.voicebot.service;

import com.vaibhavtodkar.voicebot.dto.AudioRequestDto;
import com.vaibhavtodkar.voicebot.dto.ChatResponseDto;

public interface ChatService {
	 public String generateSpeechBase64(String text);
	 ChatResponseDto chat(AudioRequestDto request) throws Exception;
}
