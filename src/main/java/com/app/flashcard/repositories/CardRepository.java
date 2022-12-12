package com.app.flashcard.repositories;

import com.app.flashcard.models.Card;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Iterator;
import java.util.List;

public interface CardRepository extends CrudRepository<Card, Integer> {
    public List<Card> findByFontContent(String fontContent);
    public List<Card> findByDeckIDOrderByRemindTimeAsc(int deckID);
    @Query("SELECT COUNT(c) FROM Card c WHERE c.status=0 AND c.deckID=:deckID")
    public int countNewCardNum(@Param("deckID") int deckID);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.deckID=:deckID AND c.status >= 1 AND c.status <= 20")
    public int countLearningCardNum(@Param("deckID") int deckID);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.deckID=:deckID AND c.status >= 21")
    public int countDueCardNum(@Param("deckID") int deckID);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.deckID=:deckID")
    public int countCardByDeckID(@Param("deckID") int deckID);

}
