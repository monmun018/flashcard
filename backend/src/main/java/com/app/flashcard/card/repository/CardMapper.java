package com.app.flashcard.card.repository;

import com.app.flashcard.card.model.CardPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * MyBatis Mapper interface for Card operations
 * Replaces the JPA CardRepository
 */
@Mapper
public interface CardMapper {
    
    // Basic CRUD operations
    CardPojo findById(@Param("id") Integer id);
    
    List<CardPojo> findByDeckId(@Param("deckId") Integer deckId);
    
    List<CardPojo> findAll();
    
    int insert(CardPojo card);
    
    int update(CardPojo card);
    
    int deleteById(@Param("id") Integer id);
    
    int deleteByDeckId(@Param("deckId") Integer deckId);
    
    // Learning queries
    List<CardPojo> findDueCards(@Param("deckId") Integer deckId, @Param("date") LocalDate date);
    
    List<CardPojo> findNewCardsByDeck(@Param("deckId") Integer deckId);
    
    List<CardPojo> findLearningCardsByDeck(@Param("deckId") Integer deckId);
    
    List<CardPojo> findByStatus(@Param("status") Integer status);
    
    // Statistics queries
    int countCardsByDeck(@Param("deckId") Integer deckId);
    
    int countNewCardsByDeck(@Param("deckId") Integer deckId);
    
    int countLearningCardsByDeck(@Param("deckId") Integer deckId);
    
    int countDueCardsByDeck(@Param("deckId") Integer deckId);
    
    int countDueCardsByDeckAndDate(@Param("deckId") Integer deckId, @Param("date") LocalDate date);
    
    // Batch operations for performance
    int batchInsert(@Param("cards") List<CardPojo> cards);
    
    int batchUpdateStatus(@Param("cardIds") List<Integer> cardIds, @Param("status") Integer status);
    
    // Advanced queries
    List<CardPojo> findCardsForReview(@Param("deckId") Integer deckId, @Param("limit") Integer limit);
    
    List<CardPojo> searchCardsByContent(@Param("deckId") Integer deckId, @Param("searchTerm") String searchTerm);
}