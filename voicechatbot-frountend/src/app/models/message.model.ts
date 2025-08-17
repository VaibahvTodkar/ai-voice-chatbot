export interface Message {
  role: 'USER' | 'ASSISTANT';
  text: string;
  timestamp: Date;
}