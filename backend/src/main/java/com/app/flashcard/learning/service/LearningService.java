package com.app.flashcard.learning.service;

import com.app.flashcard.card.model.Card;
import com.app.flashcard.learning.model.LearningLog;
import com.app.flashcard.card.repository.CardRepository;
import com.app.flashcard.learning.repository.LearningLogRepository;
import com.app.flashcard.shared.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

@Service
@Transactional
public class LearningService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private LearningLogRepository learningLogRepository;

    /**
     * Process answer for a card and update learning progress
     * @param cardID Card ID that was answered
     * @param answer Answer value (1=Again, 2=Hard, 3=Good, 4=Easy)
     * @param userID User ID who answered
     * @param deckID Deck ID containing the card
     */
    public void processAnswer(int cardID, int answer, int userID, int deckID) {
        // Update card status and remind time
        updateCardAfterAnswer(cardID, answer);
        
        // Update learning log
        updateLearningLog(deckID, userID);
    }

    /**
     * Update card status and remind time based on answer
     * @param cardID Card ID to update
     * @param answer Answer value (1=Again, 2=Hard, 3=Good, 4=Easy)
     */
    public void updateCardAfterAnswer(int cardID, int answer) {
        Optional<Card> cardOpt = cardRepository.findById(cardID);
        if (cardOpt.isEmpty()) {
            throw new EntityNotFoundException("Card not found with ID: " + cardID);
        }
        
        Card card = cardOpt.get();
        
        // Calculate new status based on spaced repetition algorithm
        int newStatus = calculateNewStatus(card.getStatus(), answer);
        card.setStatus(newStatus);
        
        // Calculate new remind time
        LocalDate newRemindTime = calculateNextRemindTime(card, answer, newStatus);
        card.setRemindTime(newRemindTime);
        
        cardRepository.save(card);
    }

    /**
     * Calculate new status based on current status and answer
     * @param currentStatus Current card status
     * @param answer Answer value (1=Again, 2=Hard, 3=Good, 4=Easy)
     * @return New status value
     */
    private int calculateNewStatus(int currentStatus, int answer) {
        if (answer == 1) {
            // Again - reset to beginning
            return 0;
        } else {
            // Hard, Good, Easy - increment status
            return currentStatus + answer;
        }
    }

    /**
     * Calculate next remind time based on card status and answer
     * @param card Current card
     * @param answer Answer value
     * @param newStatus New status after answer
     * @return New remind time
     */
    private LocalDate calculateNextRemindTime(Card card, int answer, int newStatus) {
        LocalDate currentRemindTime = card.getRemindTime();
        LocalDate today = LocalDate.now();
        
        // If answer is "Again" and card is due in future, set to today
        if (answer == 1 && currentRemindTime.isAfter(today)) {
            return today.plusDays(newStatus);
        }
        
        // Add status days to current remind time
        return currentRemindTime.plusDays(newStatus);
    }

    /**
     * Update or create learning log for today's session
     * @param deckID Deck ID
     * @param userID User ID
     */
    public void updateLearningLog(int deckID, int userID) {
        LocalDate today = LocalDate.now();
        
        // Find existing log for today
        Iterator<LearningLog> logs = learningLogRepository
                .findByDeckIDAndUserIDAndLogTime(deckID, userID, today)
                .iterator();
        
        LearningLog log;
        if (logs.hasNext()) {
            // Update existing log
            log = logs.next();
            log.increaseLearnTime();
        } else {
            // Create new log
            log = new LearningLog();
            log.setDeckID(deckID);
            log.setUserID(userID);
            log.setLogTime(today);
            log.setLearnTime(1); // First card of the day
        }
        
        learningLogRepository.save(log);
    }

    /**
     * Get today's learning log for user and deck
     * @param deckID Deck ID
     * @param userID User ID
     * @return Today's learning log, or null if not found
     */
    @Transactional(readOnly = true)
    public LearningLog getTodayLog(int deckID, int userID) {
        LocalDate today = LocalDate.now();
        Iterator<LearningLog> logs = learningLogRepository
                .findByDeckIDAndUserIDAndLogTime(deckID, userID, today)
                .iterator();
        
        return logs.hasNext() ? logs.next() : null;
    }

    /**
     * Get learning statistics for a user and deck
     * @param deckID Deck ID
     * @param userID User ID
     * @return Learning statistics summary
     */
    @Transactional(readOnly = true)
    public LearningStatistics getLearningStatistics(int deckID, int userID) {
        LearningLog todayLog = getTodayLog(deckID, userID);
        int cardsStudiedToday = todayLog != null ? todayLog.getLearnTime() : 0;
        
        // You can add more statistics here like:
        // - Total cards studied this week/month
        // - Average daily study time
        // - Streak days
        
        return new LearningStatistics(cardsStudiedToday);
    }

    /**
     * Check if user has studied any cards today for given deck
     * @param deckID Deck ID
     * @param userID User ID
     * @return true if user has studied today
     */
    @Transactional(readOnly = true)
    public boolean hasStudiedToday(int deckID, int userID) {
        LearningLog todayLog = getTodayLog(deckID, userID);
        return todayLog != null && todayLog.getLearnTime() > 0;
    }

    /**
     * Get total cards studied today across all decks for user
     * @param userID User ID
     * @return Total cards studied today
     */
    @Transactional(readOnly = true)
    public int getTotalCardsStudiedToday(int userID) {
        LocalDate today = LocalDate.now();
        // This would require a new repository method
        // For now, returning 0 as placeholder
        return 0;
    }

    /**
     * Inner class for learning statistics
     */
    public static class LearningStatistics {
        private final int cardsStudiedToday;
        
        public LearningStatistics(int cardsStudiedToday) {
            this.cardsStudiedToday = cardsStudiedToday;
        }
        
        public int getCardsStudiedToday() {
            return cardsStudiedToday;
        }
    }
}