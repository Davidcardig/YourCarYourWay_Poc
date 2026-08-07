# Chat Temps Réel - PoC Angular

## Overview
Implémentation d'une fonctionnalité de chat en temps réel pour la PoC "Your Car Your Way".

## Architecture

### Structure
```
src/app/
├── models/
│   └── chat.model.ts          # Interfaces Conversation et Message
├── services/
│   └── chat.service.ts        # Logique métier du chat
└── components/
    └── chat/
        ├── chat.component.ts  # Composant principal
        ├── chat.component.html
        └── chat.component.css
```

### Modèles
- **Conversation**: Représente une session de chat (client, canal, statut)
- **Message**: Représente un message (contenu, expéditeur CLIENT/AGENT, horodatage)

## Fonctionnalités

✅ Création de conversation  
✅ Envoi de messages en temps réel  
✅ Polling automatique (2s par défaut)  
✅ Interface utilisateur minimaliste  
✅ Gestion du cycle de vie (unsubscribe)  
✅ Fermeture de conversation  

## API Backend Requise

```
POST   /api/chat/conversations
GET    /api/chat/conversations/{id}
GET    /api/chat/conversations/{id}/messages
POST   /api/chat/conversations/{id}/messages
PATCH  /api/chat/conversations/{id}
```

### Payload Exemple

**Créer une conversation:**
```json
{
  "clientId": "uuid-client",
  "canal": "CHAT"
}
```

**Envoyer un message:**
```json
{
  "contenu": "Bonjour",
  "expediteur": "CLIENT"
}
```

## Configuration

**Base URL API** (à configurer dans `chat.service.ts`):
```typescript
private apiUrl = 'http://localhost:8080/api/chat';
```

**Intervalle de polling** (par défaut 2000ms):
```typescript
this.chatService.startPolling(conversationId, 2000);
```

## Utilisation

1. **Accéder au chat**: http://localhost:4200/chat
2. **Envoyer un message**: Taper et cliquer "Envoyer" ou Entrée
3. **Fermer le chat**: Cliquer le bouton ✕

## Bonnes Pratiques Implémentées

✓ **Composant Standalone** - Pas de NgModule requis  
✓ **Reactive RxJS** - Gestion d'état avec observables  
✓ **Memory Leaks Prevention** - Unsubscribe automatique (takeUntil)  
✓ **HttpClient** - Communication REST  
✓ **Error Handling** - Try-catch sur les requêtes  
✓ **Minimal UI** - Focus sur la fonctionnalité  
✓ **Type Safety** - Interfaces TypeScript  

## Notes PoC

- Polling au lieu de WebSocket (plus simple pour PoC)
- ClientId hardcodé (à remplacer par authentification)
- Pas de persistence locale
- Interface CSS minimale mais fonctionnelle

## Dépendances Angular

```
@angular/core
@angular/common
@angular/forms
@angular/common/http
rxjs
```
