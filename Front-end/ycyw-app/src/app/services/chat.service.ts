import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, interval } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Conversation, Message } from '../models/chat.model';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:9090/api/chat';
  private messagesSubject = new BehaviorSubject<Message[]>([]);
  public messages$ = this.messagesSubject.asObservable();

  constructor(private http: HttpClient) {}

  createConversation(clientId: string): Observable<Conversation> {
    return this.http.post<Conversation>(`${this.apiUrl}/conversations`, {
      clientId,
      canal: 'CHAT'
    });
  }

  getConversation(conversationId: string): Observable<Conversation> {
    return this.http.get<Conversation>(`${this.apiUrl}/conversations/${conversationId}`);
  }

  sendMessage(conversationId: string, contenu: string): Observable<Message> {
    return this.http.post<Message>(`${this.apiUrl}/conversations/${conversationId}/messages`, {
      contenu,
      expediteur: 'CLIENT'
    });
  }

  getMessages(conversationId: string): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.apiUrl}/conversations/${conversationId}/messages`);
  }

  startPolling(conversationId: string, intervalMs: number = 2000): void {
    interval(intervalMs)
      .pipe(
        switchMap(() => this.getMessages(conversationId))
      )
      .subscribe(messages => this.messagesSubject.next(messages));
  }

  closeConversation(conversationId: string): Observable<Conversation> {
    return this.http.patch<Conversation>(
      `${this.apiUrl}/conversations/${conversationId}`,
      { statut: 'FERMEE' }
    );
  }
}
