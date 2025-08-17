package com.vaibhavtodkar.voicebot.service.impl;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OllamaClient {

	@Value("${app.ollama.base-url}")
	private String baseUrl;

	@Value("${app.ollama.model}")
	private String model;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RestTemplate restTemplate = new RestTemplate();

	public String generate(String prompt) {
		try {
			restTemplate.getMessageConverters().stream().filter(c -> c instanceof StringHttpMessageConverter)
					.forEach(c -> ((StringHttpMessageConverter) c).setDefaultCharset(StandardCharsets.UTF_8));
			String url = baseUrl.endsWith("/") ? baseUrl + "api/generate" : baseUrl + "/api/generate";
			String payload = objectMapper.createObjectNode().put("model", model).put("prompt", prompt)
					.put("stream", false).toString();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<>(payload, headers), String.class);

			StringBuilder out = new StringBuilder();
			if (resp.getBody() != null) {
				JsonNode node = objectMapper.readTree(resp.getBody());
				if (node.has("response")) {
					return node.get("response").asText(); // this preserves emojis
				}
			}
			return out.toString().trim();
		} catch (Exception e) {
			throw new RuntimeException("Failed to call Ollama: " + e.getMessage(), e);
		}
	}
}
