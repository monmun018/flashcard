package com.app.flashcard.deck.service;

import com.app.flashcard.deck.form.DeckForm;
import com.app.flashcard.deck.model.Deck;
import com.app.flashcard.deck.repository.DeckRepository;
import com.app.flashcard.card.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeckServiceTest {

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private DeckService deckService;

    private Deck testDeck;
    private DeckForm testDeckForm;

    @BeforeEach
    void setUp() {
        testDeck = new Deck();
        testDeck.setDeckID(1);
        testDeck.setUserID(100);
        testDeck.setDeckName("Test Deck");
        testDeck.setNewCardNum(5);
        testDeck.setLearningCardNum(3);
        testDeck.setDueCardNum(2);

        testDeckForm = new DeckForm();
        testDeckForm.setDeckName("Test Deck");
    }

    @Test
    void testCreateDeck_Success() {
        // Given
        when(deckRepository.save(any(Deck.class))).thenReturn(testDeck);

        // When
        deckService.createDeck(testDeckForm, 100);

        // Then
        verify(deckRepository).save(any(Deck.class));
    }

    @Test
    void testGetDecksByUserWithStatistics_Success() {
        // Given
        List<Deck> mockDecks = Arrays.asList(testDeck);
        when(deckRepository.findByUserID(100)).thenReturn(mockDecks);
        when(cardRepository.countNewCardNum(1)).thenReturn(5); // new cards
        when(cardRepository.countLearningCardNum(1)).thenReturn(3); // learning cards
        when(cardRepository.countDueCardNum(1)).thenReturn(2); // due cards

        // When
        List<Deck> result = deckService.getDecksByUserWithStatistics(100);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        Deck deck = result.get(0);
        assertEquals("Test Deck", deck.getDeckName());
        verify(cardRepository).countNewCardNum(1);
        verify(cardRepository).countLearningCardNum(1);
        verify(cardRepository).countDueCardNum(1);
    }

    @Test
    void testGetDecksByUserWithStatistics_EmptyList() {
        // Given
        when(deckRepository.findByUserID(100)).thenReturn(Arrays.asList());

        // When
        List<Deck> result = deckService.getDecksByUserWithStatistics(100);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDeckOptionsForUser_Success() {
        // Given
        List<Deck> mockDecks = Arrays.asList(testDeck);
        when(deckRepository.findByUserID(100)).thenReturn(mockDecks);

        // When
        Map<Integer, String> result = deckService.getDeckOptionsForUser(100);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(1));
        assertEquals("Test Deck", result.get(1));
    }

    @Test
    void testGetDeckOptionsForUser_EmptyList() {
        // Given
        when(deckRepository.findByUserID(100)).thenReturn(Arrays.asList());

        // When
        Map<Integer, String> result = deckService.getDeckOptionsForUser(100);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDeckOptionsForUser_MultipleDecks() {
        // Given
        Deck deck2 = new Deck();
        deck2.setDeckID(2);
        deck2.setDeckName("Second Deck");
        
        List<Deck> mockDecks = Arrays.asList(testDeck, deck2);
        when(deckRepository.findByUserID(100)).thenReturn(mockDecks);

        // When
        Map<Integer, String> result = deckService.getDeckOptionsForUser(100);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Deck", result.get(1));
        assertEquals("Second Deck", result.get(2));
    }

    @Test
    void testDeleteDeck_Success() {
        // Given
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(1)).thenReturn(Arrays.asList());

        // When
        deckService.deleteDeck(1);

        // Then
        verify(cardRepository).findByDeckIDOrderByRemindTimeAsc(1);
        verify(cardRepository).deleteAll(any());
        verify(deckRepository).deleteById(1);
    }

    @Test
    void testDeleteDeck_CascadeDelete() {
        // Given
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(1)).thenReturn(Arrays.asList());

        // When
        deckService.deleteDeck(1);

        // Then
        // Verify that cards are deleted first, then deck
        verify(cardRepository).deleteAll(any());
        verify(deckRepository).deleteById(1);
    }
}