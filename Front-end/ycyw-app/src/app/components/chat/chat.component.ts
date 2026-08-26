import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ChatService } from '../../services/chat.service';
import { AuthService, AuthResponse } from '../../services/auth.service';
import { Message, Conversation } from '../../models/chat.model';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, OnDestroy {
  conversation = signal<Conversation | null>(null);
  conversations = signal<Conversation[]>([]);
  messages = signal<Message[]>([]);
  newMessage = signal('');
  isLoading = signal(false);
  isLoadingList = signal(false);
  initError = signal('');
  popinMessage = signal('');
  isSubjectPopinOpen = signal(false);
  newConversationSubject = signal('');
  private destroy$ = new Subject<void>();
  private userId: string = '';
  currentUser = signal<AuthResponse | null>(null);

  constructor(
    private chatService: ChatService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.chatService.messages$
      .pipe(takeUntil(this.destroy$))
      .subscribe(msgs => this.messages.set(msgs));

    const user = this.authService.getCurrentUser();
    if (user) {
      if (!user.role) {
        user.role = 'CLIENT';
      }
      this.currentUser.set(user);
      this.userId = user.userId;
      this.loadConversations();
      return;
    }

    this.initError.set('Session invalide. Veuillez vous reconnecter.');
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.chatService.stopPolling();
    this.chatService.disconnect();
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadConversations(): void {
    if (!this.currentUser()) {
      return;
    }

    this.isLoadingList.set(true);
    this.initError.set('');
    this.chatService.getUserConversations(this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (conversations) => {
          this.conversations.set(conversations);
          this.isLoadingList.set(false);
        },
        error: (err) => {
          console.error('Erreur chargement conversations:', err);
          if (err instanceof HttpErrorResponse && err.status === 404) {
            this.authService.logout();
            this.router.navigate(['/login']);
            return;
          }

          this.initError.set('Impossible de charger les conversations.');
          this.isLoadingList.set(false);
        }
      });
  }

  createConversation(): void {
    const user = this.currentUser();
    if (!user || user.role !== 'CLIENT' || this.isLoading()) {
      return;
    }
    if (this.conversations().length > 0) {
      this.showPopin('Vous avez déjà une conversation ouverte. Fermez-la avant d’en créer une autre.');
      return;
    }
    this.newConversationSubject.set('');
    this.isSubjectPopinOpen.set(true);
  }

  confirmCreateConversation(): void {
    const user = this.currentUser();
    const sujet = this.newConversationSubject().trim();
    if (!user || user.role !== 'CLIENT' || this.isLoading()) {
      return;
    }
    if (!sujet) {
      this.showPopin('Veuillez saisir le sujet de la conversation.');
      return;
    }

    this.isLoading.set(true);
    this.chatService.createConversation(this.userId, sujet)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (conversation) => {
          this.isSubjectPopinOpen.set(false);
          this.conversations.update(current => [conversation, ...current]);
          this.openConversation(conversation);
          this.isLoading.set(false);
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 409) {
            this.showPopin('Vous avez déjà une conversation ouverte. Fermez-la avant d’en créer une autre.');
          } else {
            this.initError.set('Impossible de créer la conversation.');
          }
          this.isLoading.set(false);
        }
      });
  }

  cancelSubjectPopin(): void {
    if (this.isLoading()) {
      return;
    }
    this.isSubjectPopinOpen.set(false);
    this.newConversationSubject.set('');
  }

  openConversation(conversation: Conversation): void {
    this.conversation.set(conversation);
    this.messages.set([]);
    this.chatService.stopPolling();
    this.chatService.connect();
    this.chatService.getMessages(conversation.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (messages) => {
          this.messages.set(messages);
          this.chatService.subscribeToConversation(conversation.id);
        }
      });
  }

  sendMessage(): void {
    const conversation = this.conversation();
    const user = this.currentUser();
    const messageContent = this.newMessage().trim();
    if (!messageContent || !conversation || this.isLoading() || !user) return;

    this.isLoading.set(true);
    this.newMessage.set('');
    const expediteur = user.role === 'AGENT' ? 'AGENT' : 'CLIENT';

    this.chatService.sendMessage(conversation.id, messageContent, expediteur)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (message) => {
          this.messages.update(current => current.some(existing => existing.id === message.id) ? current : [...current, message]);
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error('Erreur envoi message:', err);
          this.newMessage.set(messageContent);
          this.isLoading.set(false);
        }
      });
  }

  closeChat(): void {
    const conversation = this.conversation();
    if (conversation) {
      this.chatService.closeConversation(conversation.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.chatService.stopPolling();
            this.conversation.set(null);
            this.messages.set([]);
            this.loadConversations();
          }
        });
    }
  }

  isAgent(): boolean {
    return this.currentUser()?.role === 'AGENT';
  }

  isClient(): boolean {
    return this.currentUser()?.role === 'CLIENT';
  }

  isSelectedConversation(conversationId: string): boolean {
    return this.conversation()?.id === conversationId;
  }

  canCreateConversation(): boolean {
    const user = this.currentUser();
    return !!user && user.role === 'CLIENT' && !this.isLoading();
  }

  getSenderLabel(message: Message): string {
    const activeConversation = this.conversation();
    if (!activeConversation) {
      return '';
    }

    if (message.expediteur === 'CLIENT') {
      return `${activeConversation.clientPrenom} ${activeConversation.clientNom}`.trim();
    }

    return activeConversation.agentPrenom?.trim() || '';
  }

  isOwnMessage(message: Message): boolean {
    const user = this.currentUser();
    if (!user) {
      return false;
    }

    if (user.role === 'CLIENT') {
      return message.expediteur === 'CLIENT';
    }

    return message.expediteur === 'AGENT';
  }

  getConnectedUserDisplay(): string {
    const user = this.currentUser();
    if (!user) {
      return '';
    }

    return `${user.prenom} ${user.nom}`.trim();
  }

  closePopin(): void {
    this.popinMessage.set('');
  }

  private showPopin(message: string): void {
    this.popinMessage.set(message);
  }

  logout(): void {
    this.chatService.stopPolling();
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
