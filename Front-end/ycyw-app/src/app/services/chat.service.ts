import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { Conversation, Message } from '../models/chat.model';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/chat';
  private websocketUrl = 'ws://localhost:8080/ws/websocket';
  private messagesSubject = new BehaviorSubject<Message[]>([]);
  public messages$ = this.messagesSubject.asObservable();
  private stompClient: Client | null = null;
  private conversationSubscription: StompSubscription | null = null;
  private activeConversationId: string | null = null;

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

  connect(): void {
    if (this.stompClient?.active) {
      return;
    }

    this.stompClient = new Client({
      brokerURL: this.websocketUrl,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: () => undefined,
      onConnect: () => {
        if (this.activeConversationId) {
          this.subscribeToConversation(this.activeConversationId);
        }
      }
    });

    this.stompClient.activate();
  }

  subscribeToConversation(conversationId: string): void {
    this.activeConversationId = conversationId;

    if (!this.stompClient) {
      this.connect();
      return;
    }

    if (!this.stompClient.active) {
      this.connect();
      return;
    }

    if (this.conversationSubscription) {
      this.conversationSubscription.unsubscribe();
      this.conversationSubscription = null;
    }

    this.conversationSubscription = this.stompClient.subscribe(
      `/topic/conversation.${conversationId}`,
      (message: IMessage) => {
        const payload = JSON.parse(message.body) as Message;
        this.messagesSubject.next(this.appendMessageIfMissing(payload));
      }
    );
  }

  startPolling(conversationId: string, intervalMs: number = 2000): void {
    this.subscribeToConversation(conversationId);
  }

  stopPolling(): void {
    if (this.conversationSubscription) {
      this.conversationSubscription.unsubscribe();
      this.conversationSubscription = null;
    }
    this.activeConversationId = null;
    this.messagesSubject.next([]);
  }

  disconnect(): void {
    this.stopPolling();
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.stompClient = null;
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

  private appendMessageIfMissing(message: Message): Message[] {
    const currentMessages = this.messagesSubject.getValue();
    const isAlreadyPresent = currentMessages.some(existingMessage => existingMessage.id === message.id);

    if (isAlreadyPresent) {
      return currentMessages;
    }

    return [...currentMessages, message];
  }
}
