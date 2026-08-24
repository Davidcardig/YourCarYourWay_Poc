package com.example.yourcarryourway.repository;

import com.example.yourcarryourway.domain.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findById(UUID id);
    List<Conversation> findByClientUserIdAndStatutOrderByDateOuvertureDesc(UUID clientUserId, String statut);
    List<Conversation> findByAgentUserIdAndStatutOrderByDateOuvertureDesc(UUID agentUserId, String statut);
    boolean existsByClientUserIdAndStatut(UUID clientUserId, String statut);
}
