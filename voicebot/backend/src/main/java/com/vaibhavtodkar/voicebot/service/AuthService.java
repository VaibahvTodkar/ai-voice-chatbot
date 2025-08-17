package com.vaibhavtodkar.voicebot.service;
import com.vaibhavtodkar.voicebot.dto.LoginRequest;
import com.vaibhavtodkar.voicebot.dto.RegisterRequest;
import com.vaibhavtodkar.voicebot.entity.User;

public interface AuthService {
	String login(LoginRequest loginDto);

    String register(RegisterRequest registerDto);

	User getUserByUsernameOrEmail(String usernameOrEmail);
}
