package com.app.flashcard.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DeckCreateRequest {
    
    @NotBlank(message = "Deck name is required")
    @Size(max = 100, message = "Deck name must not exceed 100 characters")
    private String deckName;
    
    public DeckCreateRequest() {}
    
    public DeckCreateRequest(String deckName) {
        this.deckName = deckName;
    }
    
    public String getDeckName() {
        return deckName;
    }
    
    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }
}