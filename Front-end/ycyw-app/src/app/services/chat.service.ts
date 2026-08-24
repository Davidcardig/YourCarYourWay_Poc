import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, Subscription, interval } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Conversation, Message } from '../models/chat.model';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/chat';
  private messagesSubject = new BehaviorSubject<Message[]>([]);
  public messages$ = this.messagesSubject.asObservable();
  private pollingSubscription: Subscription | null = null;

  constructor(private http: HttpClient) {}

  createConversation(userId: string, sujet: string): Observable<Conversation> {
    return this.http.post<Conversation>(`${this.apiUrl}/conversations`, {
      userId,
      sujet
    });
  }

  getUserConversations(userId: string): Observable<Conversation[]> {
    return this.http.get<Conversation[]>(`${this.apiUrl}/conversations?userId=${userId}`);
  }

  getConversation(conversationId: string): Observable<Conversation> {
    return this.http.get<Conversation>(`${this.apiUrl}/conversations/${conversationId}`);
  }

  sendMessage(conversationId: string, contenu: string, expediteur: 'CLIENT' | 'AGENT'): Observable<Message> {
    return this.http.post<Message>(`${this.apiUrl}/conversations/${conversationId}/messages`, {
      contenu,
      expediteur
    });
  }

  getMessages(conversationId: string): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.apiUrl}/conversations/${conversationId}/messages`);
  }

  startPolling(conversationId: string, intervalMs: number = 2000): void {
    this.stopPolling();
    this.pollingSubscription = interval(intervalMs)
      .pipe(
        switchMap(() => this.getMessages(conversationId))
      )
      .subscribe(messages => this.messagesSubject.next(messages));
  }

  stopPolling(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
      this.pollingSubscription = null;
      this.messagesSubject.next([]);
    }
  }

  closeConversation(conversationId: string): Observable<Conversation> {
    return this.http.patch<Conversation>(
      `${this.apiUrl}/conversations/${conversationId}`,
      { statut: 'FERMEE' }
    );
  }

  assignConversation(conversationId: string, agentUserId: string): Observable<Conversation> {
    return this.http.patch<Conversation>(
      `${this.apiUrl}/conversations/${conversationId}/assign`,
      { agentUserId }
    );
  }
}
