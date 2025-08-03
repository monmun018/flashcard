package com.app.flashcard.card.service;

import com.app.flashcard.card.form.CardForm;
import com.app.flashcard.card.model.Card;
import com.app.flashcard.card.repository.CardRepository;
import com.app.flashcard.shared.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    /**
     * Get the next card to study in a deck (ordered by remind time)
     * @param deckID Deck ID
     * @return Next card to study, or null if no cards available
     */
    @Transactional(readOnly = true)
    public Card getNextCardForDeck(int deckID) {
        Iterator<Card> cards = cardRepository.findByDeckIDOrderByRemindTimeAsc(deckID).iterator();
        return cards.hasNext() ? cards.next() : null;
    }

    /**
     * Create a new card from CardForm
     * @param form CardForm containing card data
     * @return Created card
     */
    public Card createCard(CardForm form) {
        Card newCard = new Card();
        newCard.setStatus(0); // New card starts with status 0
        newCard.setDeckID((int) form.getDeckID());
        newCard.setFontContent(form.getFontContent());
        newCard.setBackContent(form.getBackContent());
        newCard.setRemindTime(LocalDate.now()); // Set initial remind time to today
        return cardRepository.save(newCard);
    }

    /**
     * Find card by ID
     * @param cardID Card ID
     * @return Card if found
     * @throws EntityNotFoundException if card not found
     */
    @Transactional(readOnly = true)
    public Card findById(int cardID) {
        Optional<Card> cardOpt = cardRepository.findById(cardID);
        if (cardOpt.isEmpty()) {
            throw new EntityNotFoundException("Card not found with ID: " + cardID);
        }
        return cardOpt.get();
    }

    /**
     * Update card after answering
     * @param cardID Card ID to update
     * @param card Updated card object
     * @return Saved card
     */
    public Card updateCard(int cardID, Card card) {
        return cardRepository.save(card);
    }

    /**
     * Get all cards in a deck ordered by remind time
     * @param deckID Deck ID
     * @return List of cards in the deck
     */
    @Transactional(readOnly = true)
    public List<Card> getCardsByDeck(int deckID) {
        return cardRepository.findByDeckIDOrderByRemindTimeAsc(deckID);
    }

    /**
     * Delete all cards in a deck
     * @param deckID Deck ID
     */
    public void deleteCardsByDeck(int deckID) {
        List<Card> cards = cardRepository.findByDeckIDOrderByRemindTimeAsc(deckID);
        cardRepository.deleteAll(cards);
    }

    /**
     * Count new cards in a deck (status = 0)
     * @param deckID Deck ID
     * @return Number of new cards
     */
    @Transactional(readOnly = true)
    public int countNewCards(int deckID) {
        return cardRepository.countNewCardNum(deckID);
    }

    /**
     * Count learning cards in a deck (status = 1-3)
     * @param deckID Deck ID
     * @return Number of learning cards
     */
    @Transactional(readOnly = true)
    public int countLearningCards(int deckID) {
        return cardRepository.countLearningCardNum(deckID);
    }

    /**
     * Count due cards in a deck (remind_time <= today)
     * @param deckID Deck ID
     * @return Number of due cards
     */
    @Transactional(readOnly = true)
    public int countDueCards(int deckID) {
        return cardRepository.countDueCardNum(deckID);
    }

    /**
     * Count total cards in a deck
     * @param deckID Deck ID
     * @return Total number of cards
     */
    @Transactional(readOnly = true)
    public int countCardsByDeck(int deckID) {
        return cardRepository.countCardByDeckID(deckID);
    }

    /**
     * Find a demo card by font content (for demo purposes)
     * @param fontContent Font content to search for
     * @return First card with matching font content, or null if not found
     */
    @Transactional(readOnly = true)
    public Card findByFontContent(String fontContent) {
        List<Card> cards = cardRepository.findByFontContent(fontContent);
        return cards.isEmpty() ? null : cards.get(0);
    }

    /**
     * Check if a deck has any cards
     * @param deckID Deck ID
     * @return true if deck has cards, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean hasDeckAnyCards(int deckID) {
        return countCardsByDeck(deckID) > 0;
    }

    /**
     * Get cards due for review today or overdue
     * @param deckID Deck ID
     * @return List of cards due for review
     */
    @Transactional(readOnly = true)
    public List<Card> getDueCards(int deckID) {
        return cardRepository.findByDeckIDAndRemindTimeLessThanEqualOrderByRemindTimeAsc(deckID, LocalDate.now());
    }

    /**
     * Update card status and remind time after learning session
     * @param cardID Card ID
     * @param newStatus New status value
     * @param newRemindTime New remind time
     * @return Updated card
     */
    public Card updateCardLearningProgress(int cardID, int newStatus, LocalDate newRemindTime) {
        Card card = findById(cardID);
        card.setStatus(newStatus);
        card.setRemindTime(newRemindTime);
        return cardRepository.save(card);
    }
}