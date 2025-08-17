package com.vaibhavtodkar.voicebot.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {
    private Long chatId;
    private Long userId;
    private String chatTitle;
}

