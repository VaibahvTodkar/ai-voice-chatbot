package com.vaibhavtodkar.voicebot.dto;

import lombok.Data;

@Data
public class AudioRequestDto {
    private String base64Audio;
    private String text;
}
