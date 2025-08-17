package com.vaibhavtodkar.voicebot.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhavtodkar.voicebot.dto.JWTAuthResponse;
import com.vaibhavtodkar.voicebot.dto.LoginRequest;
import com.vaibhavtodkar.voicebot.dto.RegisterRequest;
import com.vaibhavtodkar.voicebot.entity.User;
import com.vaibhavtodkar.voicebot.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired 
    private AuthService authService;
    
    

    // Build Login REST API
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginRequest loginDto){
        String token = authService.login(loginDto);
        
        User user = authService.getUserByUsernameOrEmail(loginDto.getUsernameOrEmail());
        
        Set<String> roles =  user
                .getRoles()
                .stream()
                .map((role) -> role.getName()).collect(Collectors.toSet());
        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);
        jwtAuthResponse.setRoles(roles);
        jwtAuthResponse.setUsername(user.getUsername());
        jwtAuthResponse.setName(user.getName());
        jwtAuthResponse.setEmail(user.getEmail());

        return ResponseEntity.ok(jwtAuthResponse);
    }

    // Build Register REST API
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerDto){
    	System.out.println("Registering user: " + registerDto.getUsername() + registerDto.getEmail() + registerDto.getPassword() + registerDto.getName());
        String response = authService.register(registerDto);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}