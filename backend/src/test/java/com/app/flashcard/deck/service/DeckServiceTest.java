package com.app.flashcard.deck.service;

import com.app.flashcard.deck.model.Deck;
import com.app.flashcard.deck.repository.DeckRepository;
import com.app.flashcard.card.repository.CardRepository;
import com.app.flashcard.card.model.Card;
import com.app.flashcard.shared.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        testDeck = new Deck();
        testDeck.setDeckID(1);
        testDeck.setUserID(100);
        testDeck.setDeckName("Test Deck");
        testDeck.setNewCardNum(5);
        testDeck.setLearningCardNum(3);
        testDeck.setDueCardNum(2);
    }

    @Test
    void testCreateDeck_Success() {
        when(deckRepository.save(any(Deck.class))).thenReturn(testDeck);

        Deck result = deckService.createDeck("Test Deck", 100);

        assertNotNull(result);
        assertEquals("Test Deck", result.getDeckName());
        assertEquals(100, result.getUserID());
        verify(deckRepository).save(any(Deck.class));
    }

    @Test
    void testGetDecksByUserWithStatistics() {
        List<Deck> decks = Arrays.asList(testDeck);
        when(deckRepository.findByUserID(100)).thenReturn(decks);
        when(cardRepository.countNewCardNum(1)).thenReturn(5);
        when(cardRepository.countLearningCardNum(1)).thenReturn(3);
        when(cardRepository.countDueCardNum(1)).thenReturn(2);
        when(deckRepository.saveAll(decks)).thenReturn(decks);

        List<Deck> result = deckService.getDecksByUserWithStatistics(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDeck, result.get(0));
        verify(deckRepository).saveAll(decks);
    }

    @Test
    void testGetDecksByUser() {
        List<Deck> decks = Arrays.asList(testDeck);
        when(deckRepository.findByUserID(100)).thenReturn(decks);

        List<Deck> result = deckService.getDecksByUser(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDeck, result.get(0));
    }

    @Test
    void testDeleteDeck() {
        List<Card> cards = Arrays.asList(new Card());
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(1)).thenReturn(cards);

        deckService.deleteDeck(1);

        verify(cardRepository).deleteAll(cards);
        verify(deckRepository).deleteById(1);
    }

    @Test
    void testUpdateDeckStatistics_Single() {
        when(cardRepository.countNewCardNum(1)).thenReturn(5);
        when(cardRepository.countLearningCardNum(1)).thenReturn(3);
        when(cardRepository.countDueCardNum(1)).thenReturn(2);

        deckService.updateDeckStatistics(testDeck);

        assertEquals(5, testDeck.getNewCardNum());
        assertEquals(3, testDeck.getLearningCardNum());
        assertEquals(2, testDeck.getDueCardNum());
    }

    @Test
    void testUpdateDeckStatistics_Multiple() {
        List<Deck> decks = Arrays.asList(testDeck);
        when(cardRepository.countNewCardNum(1)).thenReturn(5);
        when(cardRepository.countLearningCardNum(1)).thenReturn(3);
        when(cardRepository.countDueCardNum(1)).thenReturn(2);
        when(deckRepository.saveAll(decks)).thenReturn(decks);

        deckService.updateDeckStatistics(decks);

        verify(deckRepository).saveAll(decks);
    }

    @Test
    void testGetDeckOptionsForUser() {
        List<Deck> decks = Arrays.asList(testDeck);
        when(deckRepository.findByUserID(100)).thenReturn(decks);

        Map<Integer, String> result = deckService.getDeckOptionsForUser(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Deck", result.get(1));
    }

    @Test
    void testFindById_Success() {
        when(deckRepository.findById(1)).thenReturn(Optional.of(testDeck));

        Deck result = deckService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getDeckID());
    }

    @Test
    void testFindById_NotFound() {
        when(deckRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> deckService.findById(999));
    }

    @Test
    void testIsDeckOwnedByUser_True() {
        when(deckRepository.findById(1)).thenReturn(Optional.of(testDeck));

        boolean result = deckService.isDeckOwnedByUser(1, 100);

        assertTrue(result);
    }

    @Test
    void testIsDeckOwnedByUser_False() {
        when(deckRepository.findById(1)).thenReturn(Optional.of(testDeck));

        boolean result = deckService.isDeckOwnedByUser(1, 999);

        assertFalse(result);
    }

    @Test
    void testGetDecksCountByUser() {
        List<Deck> decks = Arrays.asList(testDeck, testDeck);
        when(deckRepository.findByUserID(100)).thenReturn(decks);

        long result = deckService.getDecksCountByUser(100);

        assertEquals(2, result);
    }

    @Test
    void testFindDecksByUserID() {
        List<Deck> decks = Arrays.asList(testDeck);
        when(deckRepository.findByUserID(100)).thenReturn(decks);

        List<Deck> result = deckService.findDecksByUserID(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDeck, result.get(0));
    }

    @Test
    void testFindByDeckID_Found() {
        when(deckRepository.findById(1)).thenReturn(Optional.of(testDeck));

        Deck result = deckService.findByDeckID(1);

        assertNotNull(result);
        assertEquals(testDeck, result);
    }

    @Test
    void testFindByDeckID_NotFound() {
        when(deckRepository.findById(999)).thenReturn(Optional.empty());

        Deck result = deckService.findByDeckID(999);

        assertNull(result);
    }

    @Test
    void testSave() {
        when(deckRepository.save(testDeck)).thenReturn(testDeck);

        Deck result = deckService.save(testDeck);

        assertNotNull(result);
        assertEquals(testDeck, result);
        verify(deckRepository).save(testDeck);
    }

    @Test
    void testDeleteByDeckID() {
        List<Card> cards = Arrays.asList(new Card());
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(1)).thenReturn(cards);

        deckService.deleteByDeckID(1);

        verify(cardRepository).deleteAll(cards);
        verify(deckRepository).deleteById(1);
    }
}