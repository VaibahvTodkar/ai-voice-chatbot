package com.vaibhavtodkar.voicebot.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;
    private Long userId;
    private String chatTitle;
    private LocalDateTime createdAt = LocalDateTime.now();
    

//    @OneToMany(mappedBy = "chatid", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Message> chatHistory = new ArrayList<>();
}
