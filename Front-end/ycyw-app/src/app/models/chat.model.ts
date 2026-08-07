export interface Message {
  id: string;
  conversationId: string;
  expediteur: 'CLIENT' | 'AGENT';
  contenu: string;
  horodatage: string;
}

export interface Conversation {
  id: string;
  clientId: string;
  canal: 'CHAT' | 'MESSAGE';
  statut: 'OUVERTE' | 'FERMEE';
  dateOuverture: string;
  messages?: Message[];
}
