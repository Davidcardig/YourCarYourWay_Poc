Backend Spring Boot Chat - Installation & Setup

# 1. PRÉREQUIS
- Java 21+
- Maven 3.9+
- PostgreSQL 13+ (ou MySQL/SQLite)
- IDE: IntelliJ IDEA ou VS Code

# 2. CONFIGURATION BASE DE DONNÉES

## PostgreSQL
```sql
CREATE DATABASE ycyw_db;
CREATE USER postgres WITH PASSWORD 'password';
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO postgres;
```

## MySQL
```sql
CREATE DATABASE ycyw_db;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON ycyw_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

# 3. CONFIGURATION APPLICATION (application.properties)

### Défaut (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/ycyw_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

### Alternative MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/ycyw_db
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# 4. DÉMARRAGE

## Via Maven
mvn clean package
mvn spring-boot:run

## Via IDE (Run Configuration)
- Main class: com.example.yourcarryourway.YourCarYourWayApplication
- Working directory: c:\Users\david\Desktop\YourCarYourWay_Poc\Back-end

## Vérification
curl http://localhost:8080/api/chat/conversations -X POST \
  -H "Content-Type: application/json" \
  -d '{"clientId": "550e8400-e29b-41d4-a716-446655440000", "canal": "CHAT"}'

# 5. CONNEXION AU FRONTEND ANGULAR
- Frontend tourne sur: http://localhost:4200
- Backend API sur: http://localhost:8080
- CORS est activé ✓

# 6. STRUCTURE DES TABLES (générées automatiquement)

CREATE TABLE conversation (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    canal VARCHAR NOT NULL,
    statut VARCHAR NOT NULL,
    date_ouverture TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE message (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES conversation(id),
    expediteur VARCHAR NOT NULL,
    contenu TEXT NOT NULL,
    horodatage TIMESTAMP WITH TIME ZONE NOT NULL
);

# 7. TESTS MANUELS (Postman/cURL)

## 1. Créer une conversation
POST http://localhost:8080/api/chat/conversations
{
  "clientId": "550e8400-e29b-41d4-a716-446655440000",
  "canal": "CHAT"
}

## 2. Récupérer la conversation
GET http://localhost:8080/api/chat/conversations/{id}

## 3. Envoyer un message
POST http://localhost:8080/api/chat/conversations/{id}/messages
{
  "expediteur": "CLIENT",
  "contenu": "Bonjour"
}

## 4. Récupérer les messages
GET http://localhost:8080/api/chat/conversations/{id}/messages

## 5. Fermer la conversation
PATCH http://localhost:8080/api/chat/conversations/{id}
{
  "statut": "FERMEE"
}

# 8. LOGS & DEBUGGING
- Logs par défaut dans console
- Activer les logs SQL:
  spring.jpa.show-sql=true
  spring.jpa.properties.hibernate.format_sql=true
  spring.jpa.properties.hibernate.use_sql_comments=true
