package com.example.yourcarryourway.controller;

import com.example.yourcarryourway.dto.ConversationDTO;
import com.example.yourcarryourway.dto.MessageDTO;
import com.example.yourcarryourway.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/conversations")
    public ResponseEntity<ConversationDTO> createConversation(@RequestBody Map<String, String> body) {
        UUID userId = UUID.fromString(body.get("userId"));
        String sujet = body.get("sujet");
        
        ConversationDTO conversation = chatService.createConversation(userId, sujet);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getUserConversations(
            @RequestParam UUID userId
    ) {
        List<ConversationDTO> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<ConversationDTO> getConversation(@PathVariable UUID id) {
        ConversationDTO conversation = chatService.getConversation(id);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable UUID id) {
        List<MessageDTO> messages = chatService.getMessages(id);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/conversations/{id}/messages")
    public ResponseEntity<MessageDTO> addMessage(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String expediteur = body.getOrDefault("expediteur", "CLIENT");
        String contenu = body.get("contenu");
        
        if (contenu == null || contenu.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        MessageDTO message = chatService.addMessage(id, expediteur, contenu);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PatchMapping("/conversations/{id}")
    public ResponseEntity<ConversationDTO> updateConversation(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String statut = body.get("statut");
        
        if ("FERMEE".equals(statut)) {
            ConversationDTO conversation = chatService.closeConversation(id);
            return ResponseEntity.ok(conversation);
        }
        
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/conversations/{id}/assign")
    public ResponseEntity<ConversationDTO> assignConversation(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        UUID agentUserId = UUID.fromString(body.get("agentUserId"));
        ConversationDTO conversation = chatService.assignConversationToAgent(id, agentUserId);
        return ResponseEntity.ok(conversation);
    }
}
