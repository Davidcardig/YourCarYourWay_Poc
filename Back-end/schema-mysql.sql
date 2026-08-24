CREATE TABLE IF NOT EXISTS app_user (
    id CHAR(36) PRIMARY KEY COMMENT 'UUID unique identifier',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT 'User email address',
    password VARCHAR(255) NOT NULL COMMENT 'Encrypted password',
    nom VARCHAR(255) NOT NULL COMMENT 'Last name',
    prenom VARCHAR(255) NOT NULL COMMENT 'First name',
    role VARCHAR(50) NOT NULL COMMENT 'User role: AGENT or CLIENT',
    date_naissance DATETIME NULL COMMENT 'Date of birth',
    langue_creation VARCHAR(10) DEFAULT 'FR' COMMENT 'Creation language',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Account creation date',
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Users table for authentication and authorization';

CREATE TABLE IF NOT EXISTS conversation (
    id CHAR(36) PRIMARY KEY COMMENT 'UUID unique identifier',
    client_user_id CHAR(36) NOT NULL COMMENT 'Client user ID (foreign key)',
    agent_user_id CHAR(36) NULL COMMENT 'Agent user ID (foreign key, nullable)',
    statut VARCHAR(50) NOT NULL COMMENT 'Status: OUVERTE or FERMEE',
    sujet VARCHAR(500) NULL COMMENT 'Conversation subject',
    date_ouverture DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Conversation start date',
    FOREIGN KEY (client_user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (agent_user_id) REFERENCES app_user(id) ON DELETE SET NULL,
    INDEX idx_client_user_id (client_user_id),
    INDEX idx_agent_user_id (agent_user_id),
    INDEX idx_statut (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Conversations between clients and agents';

CREATE TABLE IF NOT EXISTS message (
    id CHAR(36) PRIMARY KEY COMMENT 'UUID unique identifier',
    conversation_id CHAR(36) NOT NULL COMMENT 'Conversation ID (foreign key)',
    expediteur VARCHAR(50) NOT NULL COMMENT 'Message sender: CLIENT or AGENT',
    contenu LONGTEXT NOT NULL COMMENT 'Message content',
    horodatage DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Message timestamp',
    FOREIGN KEY (conversation_id) REFERENCES conversation(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_horodatage (horodatage),
    INDEX idx_expediteur (expediteur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Messages within conversations';

CREATE INDEX idx_conversation_date ON conversation(date_ouverture);
CREATE INDEX idx_message_conversation_date ON message(conversation_id, horodatage);
