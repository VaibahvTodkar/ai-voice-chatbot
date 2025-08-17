package com.vaibhavtodkar.voicebot.service;

public interface VoiceService {
	public String textToSpeechBase64(String text);
	public String speechToText(byte[] audioBytes);
}
