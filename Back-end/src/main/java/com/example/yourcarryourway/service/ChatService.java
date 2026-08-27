package com.example.yourcarryourway.service;

import com.example.yourcarryourway.domain.entities.Conversation;
import com.example.yourcarryourway.domain.entities.Message;
import com.example.yourcarryourway.domain.entities.User;
import com.example.yourcarryourway.domain.enums.UserRole;
import com.example.yourcarryourway.dto.ConversationDTO;
import com.example.yourcarryourway.dto.MessageDTO;
import com.example.yourcarryourway.repository.ConversationRepository;
import com.example.yourcarryourway.repository.MessageRepository;
import com.example.yourcarryourway.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private static final String OPEN_STATUS = "OUVERTE";
    private static final String CLOSED_STATUS = "FERMEE";

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ConversationRepository conversationRepository, UserRepository userRepository, MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public ConversationDTO createConversation(UUID userId, String sujet) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));

        if (user.getRole() != UserRole.CLIENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only CLIENT can create a conversation");
        }
        if (sujet == null || sujet.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subject is required");
        }
        if (conversationRepository.existsByClientUserIdAndStatut(userId, OPEN_STATUS)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client already has an open conversation");
        }

        Conversation conversation = new Conversation(user, sujet.trim());
        userRepository.findFirstByRole(UserRole.AGENT).ifPresent(conversation::setAgentUser);
        conversationRepository.save(conversation);
        ConversationDTO dto = toDTO(conversation);
        messagingTemplate.convertAndSend("/topic/conversations.agents", dto);
        return dto;
    }

    public List<ConversationDTO> getUserConversations(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
        UserRole parsedRole = user.getRole();

        if (parsedRole == UserRole.CLIENT) {
            return conversationRepository.findByClientUserIdAndStatutOrderByDateOuvertureDesc(userId, OPEN_STATUS)
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }

        return conversationRepository.findByAgentUserIdAndStatutOrderByDateOuvertureDesc(userId, OPEN_STATUS)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ConversationDTO assignConversationToAgent(UUID conversationId, UUID agentUserId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found: " + conversationId));

        User agentUser = userRepository.findById(agentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + agentUserId));

        if (agentUser.getRole() != UserRole.AGENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only AGENT can be assigned");
        }

        conversation.setAgentUser(agentUser);
        conversationRepository.save(conversation);
        ConversationDTO dto = toDTO(conversation);
        messagingTemplate.convertAndSend("/topic/conversations.agents", dto);
        return dto;
    }

    public ConversationDTO getConversation(UUID conversationId) {
        return conversationRepository.findById(conversationId)
                .map(this::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found: " + conversationId));
    }

    public List<MessageDTO> getMessages(UUID conversationId) {
        return messageRepository.findByConversationIdOrderByHorodatageAsc(conversationId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MessageDTO addMessage(UUID conversationId, String expediteur, String contenu) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found: " + conversationId));
        
        Message message = new Message(conversation, expediteur, contenu);
        messageRepository.save(message);
        return toDTO(message);
    }

    public ConversationDTO closeConversation(UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found: " + conversationId));
        
        conversation.setStatut(CLOSED_STATUS);
        conversationRepository.save(conversation);
        ConversationDTO dto = toDTO(conversation);
        messagingTemplate.convertAndSend("/topic/conversations.agents", dto);
        messagingTemplate.convertAndSend("/topic/conversation." + conversationId, dto);
        return dto;
    }

    private ConversationDTO toDTO(Conversation conversation) {
        return new ConversationDTO(
                conversation.getId(),
                conversation.getClientUser().getId(),
                conversation.getAgentUser() != null ? conversation.getAgentUser().getId() : null,
                conversation.getClientUser().getNom(),
                conversation.getClientUser().getPrenom(),
                conversation.getAgentUser() != null ? conversation.getAgentUser().getPrenom() : null,
                conversation.getSujet(),
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
