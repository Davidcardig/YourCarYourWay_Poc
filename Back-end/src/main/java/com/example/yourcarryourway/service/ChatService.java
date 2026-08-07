package com.example.yourcarryourway.service;

import com.example.yourcarryourway.domain.entities.Conversation;
import com.example.yourcarryourway.domain.entities.Message;
import com.example.yourcarryourway.dto.ConversationDTO;
import com.example.yourcarryourway.dto.MessageDTO;
import com.example.yourcarryourway.repository.ConversationRepository;
import com.example.yourcarryourway.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {
    
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public ConversationDTO createConversation(UUID clientId, String canal) {
        Conversation conversation = new Conversation(clientId, canal);
        conversationRepository.save(conversation);
        return toDTO(conversation);
    }

    public ConversationDTO getConversation(UUID conversationId) {
        return conversationRepository.findById(conversationId)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));
    }

    public List<MessageDTO> getMessages(UUID conversationId) {
        return messageRepository.findByConversationIdOrderByHorodatageAsc(conversationId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MessageDTO addMessage(UUID conversationId, String expediteur, String contenu) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));
        
        Message message = new Message(conversation, expediteur, contenu);
        messageRepository.save(message);
        return toDTO(message);
    }

    public ConversationDTO closeConversation(UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));
        
        conversation.setStatut("FERMEE");
        conversationRepository.save(conversation);
        return toDTO(conversation);
    }

    private ConversationDTO toDTO(Conversation conversation) {
        return new ConversationDTO(
                conversation.getId(),
                conversation.getClientId(),
                conversation.getCanal(),
                conversation.getStatut(),
                conversation.getDateOuverture()
        );
    }

    private MessageDTO toDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getConversation().getId(),
                message.getExpediteur(),
                message.getContenu(),
                message.getHorodatage()
        );
    }
}
