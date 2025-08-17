import { Component } from '@angular/core';
import { Message } from '../models/message.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatwindowComponent } from "./chatwindow/chatwindow.component";
interface Chat {
  name: string;
  id: number;
}

@Component({
  selector: 'app-chat',
  imports: [CommonModule, FormsModule, ChatwindowComponent],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent {
  chatHistory: Chat[] = [
  
  ];
  selectedContact: Chat | null = this.chatHistory[0];
  messages: Message[] = [
    { role: 'ASSISTANT', text: 'Hello! How can I help you?', timestamp: new Date() }
  ];
  newMessage: string = '';

  selectContact(contact: Chat) {
    this.selectedContact = contact;
    this.messages = [
      { role: 'ASSISTANT', text: `Hi ${contact.name}, how can I help you?`, timestamp: new Date() }
    ];
  }

  sendMessage() {
    if (!this.newMessage.trim()) return;
    this.messages.push({
      role: 'USER',
      text: this.newMessage,
      timestamp: new Date()
    });

    setTimeout(() => {
      this.messages.push({
        role: 'ASSISTANT',
        text: 'This is a bot reply.',
        timestamp: new Date()
      });
    }, 1000);
    this.newMessage = '';
  }
}
