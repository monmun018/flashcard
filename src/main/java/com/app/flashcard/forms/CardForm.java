package com.app.flashcard.forms;

import javax.validation.constraints.NotEmpty;

public class CardForm {
    private Integer deckID;
    private String fontContent;
    private String backContent;

    public CardForm(){}

    public CardForm(int deckID, String fontContent, String backContent) {
        this.deckID = deckID;
        this.fontContent = fontContent;
        this.backContent = backContent;
    }

    public Integer getDeckID() {
        return deckID;
    }

    public void setDeckID(Integer deckID) {
        this.deckID = deckID;
    }

    public String getFontContent() {
        return fontContent;
    }

    public void setFontContent(String fontContent) {
        this.fontContent = fontContent;
    }

    public String getBackContent() {
        return backContent;
    }

    public void setBackContent(String backContent) {
        this.backContent = backContent;
    }
}
