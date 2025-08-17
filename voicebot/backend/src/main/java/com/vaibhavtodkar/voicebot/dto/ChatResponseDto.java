package com.vaibhavtodkar.voicebot.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatResponseDto {
	private String textReqest;
    private String textResponse;
    private String audioBase64;
    private LocalDateTime timestamp = LocalDateTime.now();
}
