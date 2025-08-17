# VoiceBot REST API

This project provides a complete **Spring Boot REST API** for user authentication, login/registration, and voice-based interactions including **Text-to-Speech (TTS)**, **Speech-to-Text (STT)**, and **Audio Chat** functionalities.

---

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [API Endpoints](#api-endpoints)
  - [Authentication](#authentication)
    - [Login](#login)
    - [Register](#register)
  - [Voice & Chat](#voice--chat)
    - [Text-to-Speech](#text-to-speech)
    - [Speech-to-Text](#speech-to-text)
    - [Chat](#chat)
- [Setup & Installation](#setup--installation)
- [Usage](#usage)
- [License](#license)

---

## Features
- Secure JWT authentication for login.
- Role-based user management.
- Register new users with name, username, email, and password.
- Convert text to Base64-encoded audio.
- Convert Base64-encoded audio to text.
- Audio-based chatbot interaction.
- UTF-8 JSON responses.
- Extensible service layer for TTS/STT/chat engines.

---

## Technologies Used
- Java 21
- Spring Boot
- Spring Security
- JSON Web Tokens (JWT)
- Local TTS/Voice Service (MaryTTS, etc.)
- Base64 Audio Encoding
- Maven

---

## API Endpoints

### Authentication
## API Endpoints
## AuthController

### Login
**URL:** `/api/auth/login` or `/api/auth/signin`  
**Method:** `POST`  
**Description:** Authenticate a user and return a JWT token along with user details and roles.

**Request Body Example:**
```json
{
  "usernameOrEmail": "user@example.com",
  "password": "password123"
}


### Register	
**URL:** `/api/auth/register` or `/api/auth/signup`  
**Method:** `POST`  
**Description:** Registers a new user.
**Request Body Example:**
```json
{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123",
  "name": "John Doe"
}



## API Endpoints
## ChatController

### Text-to-Speech
**URL:** `/api/v1/chat/text-to-speech`  
**Method:** `POST`  
**Description:** Converts a text string into Base64-encoded audio.

**Request Body Example:**
```json
{
  "text": "Hello, how are you?"
}



### Speech-to-text
**URL:** `/api/v1/chat/text-to-speech`  
**Method:** `POST`  
**Description:** Converts a Base64-encoded audio into string text.

**Request Body Example:**
```json
{
  "base64Audio": "UklGRiQAAABXQVZFZm10IBAAAAABAAEA..."
}


### Chat
**URL:** `/api/v1/chat/chat`  
**Method:** `POST`  
**Description:** Sends an audio-based chat request (Base64) or text  and receives a chat response in text and audio.

**Request Body Example:**
```json
{
  "base64Audio": "UklGRiQAAABXQVZFZm10IBAAAAABAAEA...",
  "text": "Hello, Chatbot!"
}

