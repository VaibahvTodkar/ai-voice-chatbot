package com.vaibhavtodkar.voicebot.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaibhavtodkar.voicebot.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	List<Chat> findByUserIdOrderByCreatedAtDesc(Long userId);

}
