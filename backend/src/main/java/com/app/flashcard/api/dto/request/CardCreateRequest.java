package com.app.flashcard.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CardCreateRequest {
    
    @NotNull(message = "Deck ID is required")
    @Positive(message = "Deck ID must be positive")
    private Integer deckId;
    
    @NotBlank(message = "Front content is required")
    @Size(max = 1000, message = "Front content must not exceed 1000 characters")
    private String frontContent;
    
    @NotBlank(message = "Back content is required")
    @Size(max = 1000, message = "Back content must not exceed 1000 characters")
    private String backContent;
    
    public CardCreateRequest() {}
    
    public CardCreateRequest(Integer deckId, String frontContent, String backContent) {
        this.deckId = deckId;
        this.frontContent = frontContent;
        this.backContent = backContent;
    }
    
    public Integer getDeckId() {
        return deckId;
    }
    
    public void setDeckId(Integer deckId) {
        this.deckId = deckId;
    }
    
    public String getFrontContent() {
        return frontContent;
    }
    
    public void setFrontContent(String frontContent) {
        this.frontContent = frontContent;
    }
    
    public String getBackContent() {
        return backContent;
    }
    
    public void setBackContent(String backContent) {
        this.backContent = backContent;
    }
}