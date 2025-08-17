package com.vaibhavtodkar.voicebot.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

		response.setStatus(HttpServletResponse.SC_OK); // Or 401 if you want UNAUTHORIZED
		response.setContentType("application/json");

		Map<String, Object> jsonResponse = new HashMap<>();
		jsonResponse.put("forcelogout", true);
		jsonResponse.put("message", authException.getMessage());

		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), jsonResponse);
	}
}
