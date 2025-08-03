import { apiClient } from '../../../shared/api/client';
import type { Deck, DeckCreateRequest } from '../../../shared/types';

export const deckService = {
  async getDecks(): Promise<Deck[]> {
    const response = await apiClient.get<Deck[]>('/decks');
    
    if (!response.success || !response.data) {
      throw new Error(response.error || 'Failed to fetch decks');
    }
    
    return response.data;
  },

  async getDeck(deckId: number): Promise<Deck> {
    const response = await apiClient.get<Deck>(`/decks/${deckId}`);
    
    if (!response.success || !response.data) {
      throw new Error(response.error || 'Failed to fetch deck');
    }
    
    return response.data;
  },

  async createDeck(deckData: DeckCreateRequest): Promise<Deck> {
    const response = await apiClient.post<Deck>('/decks', deckData);
    
    if (!response.success || !response.data) {
      throw new Error(response.error || 'Failed to create deck');
    }
    
    return response.data;
  },

  async updateDeck(deckId: number, deckData: DeckCreateRequest): Promise<Deck> {
    const response = await apiClient.put<Deck>(`/decks/${deckId}`, deckData);
    
    if (!response.success || !response.data) {
      throw new Error(response.error || 'Failed to update deck');
    }
    
    return response.data;
  },

  async deleteDeck(deckId: number): Promise<void> {
    const response = await apiClient.delete(`/decks/${deckId}`);
    
    if (!response.success) {
      throw new Error(response.error || 'Failed to delete deck');
    }
  },
};