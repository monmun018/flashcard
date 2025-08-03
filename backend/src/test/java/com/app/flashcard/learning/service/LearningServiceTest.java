package com.app.flashcard.learning.service;

import com.app.flashcard.card.model.Card;
import com.app.flashcard.card.repository.CardRepository;
import com.app.flashcard.learning.model.LearningLog;
import com.app.flashcard.learning.repository.LearningLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private LearningLogRepository learningLogRepository;

    @InjectMocks
    private LearningService learningService;

    private Card testCard;

    @BeforeEach
    void setUp() {
        testCard = new Card();
        testCard.setCardID(1);
        testCard.setDeckID(100);
        testCard.setStatus(0); // new card
        testCard.setRemindTime(LocalDate.now());
    }

    @Test
    void testProcessAnswer_Success() {
        // Given
        when(cardRepository.findById(1)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);
        when(learningLogRepository.findByDeckIDAndUserIDAndLogTime(100, 200, LocalDate.now()))
            .thenReturn(Collections.emptyList());
        when(learningLogRepository.save(any(LearningLog.class))).thenReturn(new LearningLog());

        // When
        learningService.processAnswer(1, 2, 200, 100);

        // Then
        verify(cardRepository).save(testCard);
        verify(learningLogRepository).save(any(LearningLog.class));
    }

    @Test
    void testProcessAnswer_AgainAnswer_ResetsCard() {
        // Given
        testCard.setStatus(5); // established card
        when(cardRepository.findById(1)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);
        when(learningLogRepository.findByDeckIDAndUserIDAndLogTime(100, 200, LocalDate.now()))
            .thenReturn(Collections.emptyList());
        when(learningLogRepository.save(any(LearningLog.class))).thenReturn(new LearningLog());

        // When
        learningService.processAnswer(1, 1, 200, 100); // answer = 1 (again)

        // Then
        verify(cardRepository).save(testCard);
        assertEquals(0, testCard.getStatus()); // Should reset to 0
    }

    @Test
    void testProcessAnswer_CardNotFound() {
        // Given
        when(cardRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        // The service throws EntityNotFoundException, which is expected behavior
        assertThrows(com.app.flashcard.shared.exception.EntityNotFoundException.class, 
            () -> learningService.processAnswer(999, 1, 200, 100));
        
        verify(cardRepository, never()).save(any());
        verify(learningLogRepository, never()).save(any());
    }

    @Test
    void testUpdateLearningLog_CreatesNewLog() {
        // Given
        when(learningLogRepository.findByDeckIDAndUserIDAndLogTime(100, 200, LocalDate.now()))
            .thenReturn(Collections.emptyList());
        when(learningLogRepository.save(any(LearningLog.class))).thenAnswer(invocation -> {
            LearningLog log = invocation.getArgument(0);
            assertEquals(200, log.getUserID());
            assertEquals(100, log.getDeckID());
            assertEquals(LocalDate.now(), log.getLogTime());
            assertEquals(1, log.getLearnTime());
            return log;
        });

        // When
        learningService.updateLearningLog(100, 200);

        // Then
        verify(learningLogRepository).save(any(LearningLog.class));
    }

    @Test
    void testHasStudiedToday_True() {
        // Given
        LearningLog log = new LearningLog();
        log.setLearnTime(5);
        when(learningLogRepository.findByDeckIDAndUserIDAndLogTime(100, 200, LocalDate.now()))
            .thenReturn(Collections.singletonList(log));

        // When
        boolean result = learningService.hasStudiedToday(100, 200);

        // Then
        assertTrue(result);
    }

    @Test
    void testHasStudiedToday_False() {
        // Given
        when(learningLogRepository.findByDeckIDAndUserIDAndLogTime(100, 200, LocalDate.now()))
            .thenReturn(Collections.emptyList());

        // When
        boolean result = learningService.hasStudiedToday(100, 200);

        // Then
        assertFalse(result);
    }

    @Test
    void testGetTodayLog_Found() {
        // Given
        LearningLog log = new LearningLog();
        when(learningLogRepository.findByDeckIDAndUserIDAndLogTime(100, 200, LocalDate.now()))
            .thenReturn(Collections.singletonList(log));

        // When
        LearningLog result = learningService.getTodayLog(100, 200);

        // Then
        assertNotNull(result);
        assertEquals(log, result);
    }

    @Test
    void testGetTodayLog_NotFound() {
        // Given
        when(learningLogRepository.findByDeckIDAndUserIDAndLogTime(100, 200, LocalDate.now()))
            .thenReturn(Collections.emptyList());

        // When
        LearningLog result = learningService.getTodayLog(100, 200);

        // Then
        assertNull(result);
    }

    @Test
    void testGetLearningStatistics() {
        // Given
        LearningLog log = new LearningLog();
        log.setLearnTime(10);
        when(learningLogRepository.findByDeckIDAndUserIDAndLogTime(100, 200, LocalDate.now()))
            .thenReturn(Collections.singletonList(log));

        // When
        LearningService.LearningStatistics stats = learningService.getLearningStatistics(100, 200);

        // Then
        assertNotNull(stats);
        assertEquals(10, stats.getCardsStudiedToday());
    }
}