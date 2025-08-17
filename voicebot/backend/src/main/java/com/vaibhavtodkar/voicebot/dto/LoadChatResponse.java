package com.vaibhavtodkar.voicebot.dto;
import java.util.List;

import com.vaibhavtodkar.voicebot.entity.Message;

import lombok.Data;
@Data
public class LoadChatResponse implements ApiResponse {
    private boolean success;
    private int status;
    private List<Message> messages;

    public LoadChatResponse(boolean success, int status, List<Message> messages) {
        this.success = success;
        this.status = status;
        this.messages = messages;
    }
}
