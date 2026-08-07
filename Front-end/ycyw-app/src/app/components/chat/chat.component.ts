import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ChatService } from '../../services/chat.service';
import { AuthService } from '../../services/auth.service';
import { Message, Conversation } from '../../models/chat.model';
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
  conversation: Conversation | null = null;
  messages: Message[] = [];
  newMessage = '';
  isLoading = false;
  private destroy$ = new Subject<void>();
  private clientId: string = '';

  constructor(
    private chatService: ChatService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.clientId = user.userId;
      this.initConversation();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initConversation(): void {
    this.isLoading = true;
    this.chatService.createConversation(this.clientId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (conv) => {
          this.conversation = conv;
          this.startPolling();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Erreur création conversation:', err);
          this.isLoading = false;
        }
      });
  }

  private startPolling(): void {
    if (this.conversation) {
      this.chatService.startPolling(this.conversation.id);
      this.chatService.messages$
        .pipe(takeUntil(this.destroy$))
        .subscribe(msgs => this.messages = msgs);
    }
  }

  sendMessage(): void {
    if (!this.newMessage.trim() || !this.conversation || this.isLoading) return;

    this.isLoading = true;
    const messageContent = this.newMessage;
    this.newMessage = '';

    this.chatService.sendMessage(this.conversation.id, messageContent)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Erreur envoi message:', err);
          this.newMessage = messageContent;
          this.isLoading = false;
        }
      });
  }

  closeChat(): void {
    if (this.conversation) {
      this.chatService.closeConversation(this.conversation.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.conversation = null;
            this.messages = [];
          }
        });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

