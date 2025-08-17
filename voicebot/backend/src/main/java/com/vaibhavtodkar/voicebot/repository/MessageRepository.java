package com.vaibhavtodkar.voicebot.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaibhavtodkar.voicebot.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

	List<Message> findByChatidAndUserId(Long chatId, long id);

}