package com.app.flashcard;

import com.app.flashcard.user.model.UserPojo;
import com.app.flashcard.card.model.CardPojo;
import com.app.flashcard.deck.model.DeckPojo;
import com.app.flashcard.learning.model.LearningLogPojo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Quick integration test to verify MyBatis migration
 */
@SpringBootTest
public class MyBatisMigrationTest {
    
    @Test
    public void testLombokIntegration() {
        // Test UserPojo
        UserPojo user = UserPojo.builder()
            .userLoginID("testuser")
            .userPW("password")
            .userName("Test User")
            .userAge(25)
            .userMail("test@example.com")
            .build();
        
        assert user.getUserLoginID().equals("testuser");
        assert user.canLoginWith("testuser");
        
        // Test CardPojo
        CardPojo card = CardPojo.builder()
            .deckID(1)
            .frontContent("Question")
            .backContent("Answer")
            .status(0)
            .build();
        
        assert card.isNew();
        assert !card.isLearning();
        
        // Test DeckPojo
        DeckPojo deck = DeckPojo.builder()
            .userID(1)
            .deckName("Test Deck")
            .newCardNum(5)
            .build();
        
        assert deck.getTotalCards() == 5;
        assert !deck.isEmpty();
        
        // Test LearningLogPojo
        LearningLogPojo log = LearningLogPojo.builder()
            .userID(1)
            .deckID(1)
            .build();
        
        assert log.getLearnTime() == 1;  // default value
    }
}