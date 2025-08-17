import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Message } from '../models/message.model';

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/chat/chat'; // Centralized API URL
  private readonly modelName = 'gemma2:2b'; // Configurable model name

  chats: any[] = [];
  messages: Message[] = []; 
  chatId: number = 0;

  constructor(private http: HttpClient) { }

  getBotReply(prompt: string): Observable<Message> {
    return this.http.post<Message>(this.apiUrl, {
      prompt: prompt,
      model: this.modelName,
      chatId: this.chatId
    }).pipe(
      catchError((error) => {
        console.error('Error in ChatbotService:', error);
        return throwError(() => new Error('Failed to fetch chatbot reply.'));
      })
    );
  }

 
}
