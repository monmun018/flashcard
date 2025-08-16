import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { cardService } from '../services/cardService';
import type { CardCreateRequest } from '../../../shared/types';

export const useCards = (deckId?: number) => {
  const queryClient = useQueryClient();

  // Get cards by deck
  const {
    data: cards = [],
    isLoading,
    error,
    refetch
  } = useQuery({
    queryKey: ['cards', deckId],
    queryFn: () => cardService.getCardsByDeck(deckId!),
    enabled: deckId !== undefined,
  });

  // Create card mutation
  const createCardMutation = useMutation({
    mutationFn: cardService.createCard,
    onSuccess: () => {
      // Invalidate cards query to refetch
      queryClient.invalidateQueries({ queryKey: ['cards', deckId] });
      // Also invalidate decks to update card counts
      queryClient.invalidateQueries({ queryKey: ['decks'] });
      queryClient.invalidateQueries({ queryKey: ['deck', deckId] });
    },
  });

  // Update card mutation
  const updateCardMutation = useMutation({
    mutationFn: ({ cardId, cardData }: { cardId: number; cardData: CardCreateRequest }) =>
      cardService.updateCard(cardId, cardData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cards', deckId] });
      queryClient.invalidateQueries({ queryKey: ['decks'] });
    },
  });

  // Delete card mutation
  const deleteCardMutation = useMutation({
    mutationFn: cardService.deleteCard,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cards', deckId] });
      queryClient.invalidateQueries({ queryKey: ['decks'] });
      queryClient.invalidateQueries({ queryKey: ['deck', deckId] });
    },
  });

  return {
    // Data
    cards,
    isLoading,
    error,
    
    // Actions
    createCard: createCardMutation.mutateAsync,
    updateCard: updateCardMutation.mutateAsync,
    deleteCard: deleteCardMutation.mutateAsync,
    refetch,
    
    // Loading states
    isCreating: createCardMutation.isPending,
    isUpdating: updateCardMutation.isPending,
    isDeleting: deleteCardMutation.isPending,
    
    // Error states
    createError: createCardMutation.error,
    updateError: updateCardMutation.error,
    deleteError: deleteCardMutation.error,
  };
};

export const useCard = (cardId?: number) => {
  const {
    data: card,
    isLoading,
    error
  } = useQuery({
    queryKey: ['card', cardId],
    queryFn: () => cardService.getCard(cardId!),
    enabled: cardId !== undefined,
  });

  return {
    card,
    isLoading,
    error,
  };
};