package com.app.flashcard.repositories;

import com.app.flashcard.models.Card;
import com.app.flashcard.models.LearningLog;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface LearningLogRepository extends CrudRepository<LearningLog, Integer> {
    public List<LearningLog> findByDeckIDAndUserIDAndLogTime(int deckID, int userID, LocalDate logTime);
}
