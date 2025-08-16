import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { deckService } from '../services/deckService';
import type { Deck, DeckCreateRequest } from '../../../shared/types';

export const useDecks = () => {
  const queryClient = useQueryClient();

  // Get all decks
  const {
    data: decks = [],
    isLoading,
    error,
    refetch
  } = useQuery({
    queryKey: ['decks'],
    queryFn: deckService.getDecks,
  });

  // Create deck mutation
  const createDeckMutation = useMutation({
    mutationFn: deckService.createDeck,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['decks'] });
    },
  });

  // Update deck mutation
  const updateDeckMutation = useMutation({
    mutationFn: ({ deckId, deckData }: { deckId: number; deckData: DeckCreateRequest }) =>
      deckService.updateDeck(deckId, deckData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['decks'] });
    },
  });

  // Delete deck mutation
  const deleteDeckMutation = useMutation({
    mutationFn: deckService.deleteDeck,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['decks'] });
    },
  });

  const createDeck = async (deckData: DeckCreateRequest): Promise<Deck> => {
    return createDeckMutation.mutateAsync(deckData);
  };

  const updateDeck = async (deckId: number, deckData: DeckCreateRequest): Promise<Deck> => {
    return updateDeckMutation.mutateAsync({ deckId, deckData });
  };

  const deleteDeck = async (deckId: number): Promise<void> => {
    return deleteDeckMutation.mutateAsync(deckId);
  };

  return {
    // Data
    decks,
    isLoading,
    error,
    
    // Actions
    createDeck,
    updateDeck,
    deleteDeck,
    refetch,
    
    // Loading states
    isCreating: createDeckMutation.isPending,
    isUpdating: updateDeckMutation.isPending,
    isDeleting: deleteDeckMutation.isPending,
    
    // Errors
    createError: createDeckMutation.error,
    updateError: updateDeckMutation.error,
    deleteError: deleteDeckMutation.error,
  };
};

export const useDeck = (deckId?: number) => {
  return useQuery({
    queryKey: ['decks', deckId],
    queryFn: () => deckService.getDeck(deckId!),
    enabled: !!deckId,
  });
};