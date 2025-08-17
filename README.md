# VoiceBot Full-Stack Application

A full-stack **VoiceBot** application with secure authentication, voice-based interactions, and a responsive frontend.  
The backend is built with **Spring Boot** and **JWT authentication**, while the frontend is developed using **Angular 19** and **Bootstrap 5**.

---

## Table of Contents
- [Frontend](#frontend)
- [Backend Features](#backend-features)
- [Technologies Used](#technologies-used)
- [Setup & Installation](#setup--installation)


---

## Frontend

The frontend is a modern, responsive web application built with **Angular 19** and **Bootstrap 5**. It provides a clean interface to interact with the VoiceBot backend APIs.

### Key Features
- **User Authentication:** Login and register with JWT token handling.
- **Voice Interaction:** Record audio, convert it to text, or generate audio from text.
- **Chat Interface:** Real-time chat with audio input and output.
- **Responsive Design:** Works on desktops, tablets, and mobile devices.
- **Session Management:** Stores user information and JWT tokens securely in session storage.
- **API Integration:** Fully connected to backend endpoints for authentication, TTS, STT, and chat.

### Technologies Used
- Angular 19
- TypeScript
- HTML5 & CSS3
- Bootstrap 5
- RxJS
- HTTPClient for API calls

---

## Backend Features
- Secure JWT authentication for login.
- Role-based user management.
- User registration with name, username, email, and password.
- Convert text to Base64-encoded audio (TTS).
- Convert Base64-encoded audio to text (STT).
- Audio-based chatbot interaction.
- UTF-8 JSON responses.
- Extensible services for TTS/STT/chat engines.

---

## Technologies Used
- Java 17+
- Spring Boot
- Spring Security
- JSON Web Tokens (JWT)
- Local TTS/Voice Service (MaryTTS, etc.)
- Base64 Audio Encoding
- Maven

---

## Setup & Installation
- Clone the reqpository
- Frontend and Backend are in separate folder 
- For Frontend Read voicechatbot-frountend folder README file.
- For Backend read voicebot/backend folder README file
