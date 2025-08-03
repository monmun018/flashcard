package com.app.flashcard.deck.service;

import com.app.flashcard.deck.form.DeckForm;
import com.app.flashcard.deck.model.Deck;
import com.app.flashcard.card.repository.CardRepository;
import com.app.flashcard.deck.repository.DeckRepository;
import com.app.flashcard.shared.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeckService {

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private CardRepository cardRepository;

    /**
     * Get all decks for a user with updated statistics
     * @param userID User ID
     * @return List of decks with current statistics
     */
    @Transactional(readOnly = true)
    public List<Deck> getDecksByUserWithStatistics(int userID) {
        List<Deck> decks = deckRepository.findByUserID(userID);
        updateDeckStatistics(decks);
        return decks;
    }

    /**
     * Get all decks for a user without updating statistics
     * @param userID User ID
     * @return List of decks
     */
    @Transactional(readOnly = true)
    public List<Deck> getDecksByUser(int userID) {
        return deckRepository.findByUserID(userID);
    }

    /**
     * Create a new deck for user
     * @param deckName Name of the deck
     * @param userID User ID who owns the deck
     * @return Created deck
     */
    public Deck createDeck(String deckName, int userID) {
        Deck newDeck = new Deck();
        newDeck.setDeckName(deckName);
        newDeck.setUserID(userID);
        return deckRepository.save(newDeck);
    }

    /**
     * Create deck from DeckForm
     * @param form DeckForm containing deck data
     * @param userID User ID who owns the deck
     * @return Created deck
     */
    public Deck createDeck(DeckForm form, int userID) {
        return createDeck(form.getDeckName(), userID);
    }

    /**
     * Delete a deck and all its cards
     * @param deckID Deck ID to delete
     */
    public void deleteDeck(int deckID) {
        // First delete all cards in the deck
        List<com.app.flashcard.card.model.Card> cards = cardRepository.findByDeckIDOrderByRemindTimeAsc(deckID);
        cardRepository.deleteAll(cards);
        
        // Then delete the deck
        deckRepository.deleteById(deckID);
    }

    /**
     * Update statistics for multiple decks
     * @param decks List of decks to update
     */
    public void updateDeckStatistics(List<Deck> decks) {
        for (Deck deck : decks) {
            updateDeckStatistics(deck);
        }
        deckRepository.saveAll(decks);
    }

    /**
     * Update statistics for a single deck
     * @param deck Deck to update
     */
    public void updateDeckStatistics(Deck deck) {
        int deckID = deck.getDeckID();
        int newCardNum = cardRepository.countNewCardNum(deckID);
        int learningCardNum = cardRepository.countLearningCardNum(deckID);
        int dueCardNum = cardRepository.countDueCardNum(deckID);
        
        deck.setNewCardNum(newCardNum);
        deck.setLearningCardNum(learningCardNum);
        deck.setDueCardNum(dueCardNum);
    }

    /**
     * Get deck options as Map for dropdown/select components
     * @param userID User ID
     * @return Map of deckID -> deckName
     */
    @Transactional(readOnly = true)
    public Map<Integer, String> getDeckOptionsForUser(int userID) {
        List<Deck> decks = deckRepository.findByUserID(userID);
        return decks.stream().collect(
            Collectors.toMap(Deck::getDeckID, Deck::getDeckName)
        );
    }

    /**
     * Find deck by ID
     * @param deckID Deck ID
     * @return Deck if found
     * @throws EntityNotFoundException if deck not found
     */
    @Transactional(readOnly = true)
    public Deck findById(int deckID) {
        return deckRepository.findById(deckID)
            .orElseThrow(() -> new EntityNotFoundException("Deck not found with ID: " + deckID));
    }

    /**
     * Check if deck belongs to user
     * @param deckID Deck ID
     * @param userID User ID
     * @return true if deck belongs to user
     */
    @Transactional(readOnly = true)
    public boolean isDeckOwnedByUser(int deckID, int userID) {
        Deck deck = findById(deckID);
        return deck.getUserID() == userID;
    }

    /**
     * Get total number of decks for user
     * @param userID User ID
     * @return Total number of decks
     */
    @Transactional(readOnly = true)
    public long getDecksCountByUser(int userID) {
        return deckRepository.findByUserID(userID).size();
    }

    // API-specific methods

    /**
     * Find all decks by user ID
     * @param userID User ID
     * @return List of decks
     */
    @Transactional(readOnly = true)
    public List<Deck> findDecksByUserID(int userID) {
        return deckRepository.findByUserID(userID);
    }

    /**
     * Find deck by ID (nullable version for API)
     * @param deckID Deck ID
     * @return Deck if found, null otherwise
     */
    @Transactional(readOnly = true)
    public Deck findByDeckID(int deckID) {
        return deckRepository.findById(deckID).orElse(null);
    }

    /**
     * Save deck entity
     * @param deck Deck to save
     * @return Saved deck
     */
    public Deck save(Deck deck) {
        return deckRepository.save(deck);
    }

    /**
     * Delete deck by ID
     * @param deckID Deck ID to delete
     */
    public void deleteByDeckID(int deckID) {
        deleteDeck(deckID);
    }
}