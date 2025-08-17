package com.vaibhavtodkar.voicebot.service.impl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaibhavtodkar.voicebot.service.VoiceService;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;

@Service
public class VoiceServiceImpl implements VoiceService {

    private final MaryInterface maryTTS;
    private final Model voskModel;

    public VoiceServiceImpl() throws Exception {
        // Initialize MaryTTS
        maryTTS = new LocalMaryInterface();
        maryTTS.setVoice("cmu-slt-hsmm");

        // Initialize Vosk Model (offline)
        voskModel = new Model("vosk-model-small-en-us-0.15"); // path to vosk model
    }

    // 🔹 Text → Speech (Base64)
    @Override
    public String textToSpeechBase64(String text) {
        try {
			if (text == null || text.isEmpty()) {
				text = "no text provided";
			}
        	System.out.println("Generating speech for text: " + text);
            AudioInputStream audio = maryTTS.generateAudio(text);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AudioSystem.write(audio, AudioFileFormat.Type.WAVE, baos);
            byte[] audioBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(audioBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating speech", e);
        }
    }


    
    
    @Override
    public String speechToText(byte[] audioBytes) {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioBytes));
             Recognizer recognizer = new Recognizer(voskModel, (int) ais.getFormat().getSampleRate())) {

            AudioFormat format = ais.getFormat();

            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                throw new RuntimeException("Only PCM signed WAV is supported by Vosk");
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = ais.read(buffer)) != -1) {
                recognizer.acceptWaveForm(buffer, bytesRead);
            }
            
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert JSON string to Map
            @SuppressWarnings("unchecked")
			Map<String, String> map = objectMapper.readValue( recognizer.getFinalResult(), Map.class);

            return map.get("text");
        } catch (Exception e) {
            throw new RuntimeException("Error recognizing speech", e);
        }
    }
}
