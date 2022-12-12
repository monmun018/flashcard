package com.app.flashcard.repositories;

import com.app.flashcard.models.Deck;
import com.app.flashcard.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeckRepository extends CrudRepository<Deck,Integer> {
    public List<Deck> findByUserID(int userID);
}
