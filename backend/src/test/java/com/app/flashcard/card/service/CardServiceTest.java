package com.app.flashcard.card.service;

import com.app.flashcard.card.model.Card;
import com.app.flashcard.card.repository.CardRepository;
import com.app.flashcard.shared.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private Card testCard;

    @BeforeEach
    void setUp() {
        testCard = new Card();
        testCard.setCardID(1);
        testCard.setDeckID(100);
        testCard.setFontContent("Hello");
        testCard.setBackContent("Xin chào");
        testCard.setStatus(0);
        testCard.setRemindTime(LocalDate.now());
    }

    @Test
    void testCreateCard_Success() {
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        Card result = cardService.createCard(100, "Hello", "Xin chào");

        assertNotNull(result);
        assertEquals(100, result.getDeckID());
        assertEquals("Hello", result.getFontContent());
        assertEquals("Xin chào", result.getBackContent());
        assertEquals(0, result.getStatus());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void testGetNextCardForDeck_WithCards() {
        List<Card> cards = Arrays.asList(testCard);
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(100)).thenReturn(cards);

        Card result = cardService.getNextCardForDeck(100);

        assertNotNull(result);
        assertEquals(testCard, result);
    }

    @Test
    void testGetNextCardForDeck_NoCards() {
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(100)).thenReturn(Collections.emptyList());

        Card result = cardService.getNextCardForDeck(100);

        assertNull(result);
    }

    @Test
    void testFindById_Success() {
        when(cardRepository.findById(1)).thenReturn(Optional.of(testCard));

        Card result = cardService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getCardID());
    }

    @Test
    void testFindById_NotFound() {
        when(cardRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> cardService.findById(999));
    }

    @Test
    void testUpdateCard() {
        when(cardRepository.save(testCard)).thenReturn(testCard);

        Card result = cardService.updateCard(1, testCard);

        assertNotNull(result);
        assertEquals(testCard, result);
        verify(cardRepository).save(testCard);
    }

    @Test
    void testGetCardsByDeck() {
        List<Card> cards = Arrays.asList(testCard);
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(100)).thenReturn(cards);

        List<Card> result = cardService.getCardsByDeck(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCard, result.get(0));
    }

    @Test
    void testDeleteCardsByDeck() {
        List<Card> cards = Arrays.asList(testCard);
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(100)).thenReturn(cards);

        cardService.deleteCardsByDeck(100);

        verify(cardRepository).deleteAll(cards);
    }

    @Test
    void testCountNewCards() {
        when(cardRepository.countNewCardNum(100)).thenReturn(5);

        int result = cardService.countNewCards(100);

        assertEquals(5, result);
    }

    @Test
    void testCountLearningCards() {
        when(cardRepository.countLearningCardNum(100)).thenReturn(3);

        int result = cardService.countLearningCards(100);

        assertEquals(3, result);
    }

    @Test
    void testCountDueCards() {
        when(cardRepository.countDueCardNum(100)).thenReturn(2);

        int result = cardService.countDueCards(100);

        assertEquals(2, result);
    }

    @Test
    void testCountCardsByDeck() {
        when(cardRepository.countCardByDeckID(100)).thenReturn(10);

        int result = cardService.countCardsByDeck(100);

        assertEquals(10, result);
    }

    @Test
    void testFindByFontContent_Found() {
        List<Card> cards = Arrays.asList(testCard);
        when(cardRepository.findByFontContent("Hello")).thenReturn(cards);

        Card result = cardService.findByFontContent("Hello");

        assertNotNull(result);
        assertEquals(testCard, result);
    }

    @Test
    void testFindByFontContent_NotFound() {
        when(cardRepository.findByFontContent("Hello")).thenReturn(Collections.emptyList());

        Card result = cardService.findByFontContent("Hello");

        assertNull(result);
    }

    @Test
    void testHasDeckAnyCards_True() {
        when(cardRepository.countCardByDeckID(100)).thenReturn(5);

        boolean result = cardService.hasDeckAnyCards(100);

        assertTrue(result);
    }

    @Test
    void testHasDeckAnyCards_False() {
        when(cardRepository.countCardByDeckID(100)).thenReturn(0);

        boolean result = cardService.hasDeckAnyCards(100);

        assertFalse(result);
    }

    @Test
    void testGetDueCards() {
        List<Card> cards = Arrays.asList(testCard);
        LocalDate today = LocalDate.now();
        when(cardRepository.findByDeckIDAndRemindTimeLessThanEqualOrderByRemindTimeAsc(100, today))
            .thenReturn(cards);

        List<Card> result = cardService.getDueCards(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCard, result.get(0));
    }

    @Test
    void testUpdateCardLearningProgress() {
        LocalDate newRemindTime = LocalDate.now().plusDays(1);
        when(cardRepository.findById(1)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        Card result = cardService.updateCardLearningProgress(1, 1, newRemindTime);

        assertNotNull(result);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void testFindCardsByDeckID() {
        List<Card> cards = Arrays.asList(testCard);
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(100)).thenReturn(cards);

        List<Card> result = cardService.findCardsByDeckID(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCard, result.get(0));
    }

    @Test
    void testFindByCardID_Found() {
        when(cardRepository.findById(1)).thenReturn(Optional.of(testCard));

        Card result = cardService.findByCardID(1);

        assertNotNull(result);
        assertEquals(testCard, result);
    }

    @Test
    void testFindByCardID_NotFound() {
        when(cardRepository.findById(999)).thenReturn(Optional.empty());

        Card result = cardService.findByCardID(999);

        assertNull(result);
    }

    @Test
    void testSave() {
        when(cardRepository.save(testCard)).thenReturn(testCard);

        Card result = cardService.save(testCard);

        assertNotNull(result);
        assertEquals(testCard, result);
        verify(cardRepository).save(testCard);
    }

    @Test
    void testDeleteByCardID() {
        cardService.deleteByCardID(1);

        verify(cardRepository).deleteById(1);
    }
}