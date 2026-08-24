export interface Message {
  id: string;
  conversationId: string;
  expediteur: 'CLIENT' | 'AGENT';
  contenu: string;
  horodatage: string;
}

export interface Conversation {
  id: string;
  clientUserId: string;
  agentUserId?: string | null;
  clientNom: string;
  clientPrenom: string;
  agentPrenom?: string | null;
  sujet: string;
  statut: 'OUVERTE' | 'FERMEE';
  dateOuverture: string;
  messages?: Message[];
}
