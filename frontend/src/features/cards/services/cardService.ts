import { apiClient } from '../../../shared/api/client';
import type { Card, CardCreateRequest } from '../../../shared/types';

export const cardService = {
  async getCardsByDeck(deckId: number): Promise<Card[]> {
    const response = await apiClient.get<Card[]>(`/cards/deck/${deckId}`);
    
    if (!response.success || !response.data) {
      throw new Error(response.error || 'Failed to fetch cards');
    }
    
    return response.data;
  },

  async getCard(cardId: number): Promise<Card> {
    const response = await apiClient.get<Card>(`/cards/${cardId}`);
    
    if (!response.success || !response.data) {
      throw new Error(response.error || 'Failed to fetch card');
    }
    
    return response.data;
  },

  async createCard(cardData: CardCreateRequest): Promise<Card> {
    const response = await apiClient.post<Card>('/cards', cardData);
    
    if (!response.success || !response.data) {
      throw new Error(response.error || 'Failed to create card');
    }
    
    return response.data;
  },

  async updateCard(cardId: number, cardData: CardCreateRequest): Promise<Card> {
    const response = await apiClient.put<Card>(`/cards/${cardId}`, cardData);
    
    if (!response.success || !response.data) {
      throw new Error(response.error || 'Failed to update card');
    }
    
    return response.data;
  },

  async deleteCard(cardId: number): Promise<void> {
    const response = await apiClient.delete(`/cards/${cardId}`);
    
    if (!response.success) {
      throw new Error(response.error || 'Failed to delete card');
    }
  },
};