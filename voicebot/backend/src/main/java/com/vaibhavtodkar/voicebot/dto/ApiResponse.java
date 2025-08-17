package com.vaibhavtodkar.voicebot.dto;
import java.time.LocalDateTime;


public interface ApiResponse {
	Boolean status = false;
	Integer code = 500;
	LocalDateTime timestamp = LocalDateTime.now();
}
