package com.app.flashcard.deck.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Deck POJO for MyBatis with Lombok
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckPojo {
    private Integer deckID;
    private Integer userID;
    private String deckName;
    
    @Builder.Default
    private Integer newCardNum = 0;
    
    @Builder.Default
    private Integer learningCardNum = 0;
    
    @Builder.Default
    private Integer dueCardNum = 0;
    
    public int getTotalCards() {
        return (newCardNum != null ? newCardNum : 0) + 
               (learningCardNum != null ? learningCardNum : 0) + 
               (dueCardNum != null ? dueCardNum : 0);
    }
    
    public boolean isEmpty() {
        return getTotalCards() == 0;
    }
}