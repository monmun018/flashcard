package com.app.flashcard.card.service;

import com.app.flashcard.card.form.CardForm;
import com.app.flashcard.card.model.Card;
import com.app.flashcard.card.repository.CardRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private Card testCard;
    private CardForm testCardForm;

    @BeforeEach
    void setUp() {
        testCard = new Card();
        testCard.setCardID(1);
        testCard.setDeckID(100);
        testCard.setFontContent("Hello");
        testCard.setBackContent("Xin chào");
        testCard.setStatus(0);
        testCard.setRemindTime(LocalDate.now());

        testCardForm = new CardForm();
        testCardForm.setDeckID(100);
        testCardForm.setFontContent("Hello");
        testCardForm.setBackContent("Xin chào");
    }

    @Test
    void testCreateCard_Success() {
        // Given
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        // When
        cardService.createCard(testCardForm);

        // Then
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void testFindByFontContent_Success() {
        // Given
        when(cardRepository.findByFontContent("Hello")).thenReturn(Arrays.asList(testCard));

        // When
        Card result = cardService.findByFontContent("Hello");

        // Then
        assertNotNull(result);
        assertEquals("Hello", result.getFontContent());
        assertEquals("Xin chào", result.getBackContent());
    }

    @Test
    void testFindByFontContent_NotFound() {
        // Given
        when(cardRepository.findByFontContent("NonExistent")).thenReturn(Collections.emptyList());

        // When
        Card result = cardService.findByFontContent("NonExistent");

        // Then
        assertNull(result);
    }

    @Test
    void testGetNextCardForDeck_WithCards() {
        // Given
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(100)).thenReturn(Arrays.asList(testCard));

        // When
        Card result = cardService.getNextCardForDeck(100);

        // Then
        assertNotNull(result);
        assertEquals(testCard, result);
        verify(cardRepository).findByDeckIDOrderByRemindTimeAsc(100);
    }

    @Test
    void testGetNextCardForDeck_NoCards() {
        // Given
        when(cardRepository.findByDeckIDOrderByRemindTimeAsc(100)).thenReturn(Collections.emptyList());

        // When
        Card result = cardService.getNextCardForDeck(100);

        // Then
        assertNull(result);
        verify(cardRepository).findByDeckIDOrderByRemindTimeAsc(100);
    }

    @Test
    void testHasDeckAnyCards_True() {
        // Given
        when(cardRepository.countCardByDeckID(100)).thenReturn(5);

        // When
        boolean result = cardService.hasDeckAnyCards(100);

        // Then
        assertTrue(result);
    }

    @Test
    void testHasDeckAnyCards_False() {
        // Given
        when(cardRepository.countCardByDeckID(100)).thenReturn(0);

        // When
        boolean result = cardService.hasDeckAnyCards(100);

        // Then
        assertFalse(result);
    }

    @Test
    void testCreateCard_SetsDefaultValues() {
        // Given
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            // Verify default values are set
            assertEquals(0, card.getStatus()); // new card
            assertEquals(LocalDate.now(), card.getRemindTime());
            return card;
        });

        // When
        cardService.createCard(testCardForm);

        // Then
        verify(cardRepository).save(any(Card.class));
    }
}