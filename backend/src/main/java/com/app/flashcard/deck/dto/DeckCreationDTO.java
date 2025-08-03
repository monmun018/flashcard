package com.app.flashcard.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for deck creation
 */
public class DeckCreationDTO {
    
    @NotBlank(message = "Tên bộ thẻ không được để trống")
    @Size(min = 1, max = 100, message = "Tên bộ thẻ phải từ 1-100 ký tự")
    private String deckName;

    public DeckCreationDTO() {}

    public DeckCreationDTO(String deckName) {
        this.deckName = deckName;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }
}