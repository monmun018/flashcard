package com.app.flashcard.deck.repository;

import com.app.flashcard.deck.model.Deck;
import com.app.flashcard.user.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeckRepository extends CrudRepository<Deck,Integer> {
    public List<Deck> findByUserID(int userID);
}