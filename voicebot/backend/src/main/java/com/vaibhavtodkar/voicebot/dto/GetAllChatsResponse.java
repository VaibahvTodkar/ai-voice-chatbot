package com.vaibhavtodkar.voicebot.dto;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class GetAllChatsResponse implements ApiResponse {
    private Boolean status;
    private Integer code;
    private List<ChatDto> chats;
    private LocalDateTime timestamp;

    public GetAllChatsResponse(Boolean status, Integer code, List<ChatDto> chats) {
        this.status = status;
        this.code = code;
        this.chats = chats;
        this.timestamp = LocalDateTime.now();
    }
}