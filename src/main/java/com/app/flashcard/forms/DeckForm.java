package com.app.flashcard.forms;

import jakarta.validation.constraints.NotEmpty;

public class DeckForm {
    @NotEmpty(message = "Hãy điền tên bộ thẻ!")
    private String deckName;

    public DeckForm() {
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }
}
