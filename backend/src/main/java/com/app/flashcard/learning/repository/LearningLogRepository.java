package com.app.flashcard.learning.repository;

import com.app.flashcard.card.model.Card;
import com.app.flashcard.learning.model.LearningLog;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface LearningLogRepository extends CrudRepository<LearningLog, Integer> {
    public List<LearningLog> findByDeckIDAndUserIDAndLogTime(int deckID, int userID, LocalDate logTime);
}