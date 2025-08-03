package com.app.flashcard.card.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CardID")
    private int cardID;
    @Column(name = "DeckID")
    private int deckID;
    @Column(name = "FontContent")
    private String fontContent;
    @Column(name = "BackContent")
    private String backContent;
    @Column(name = "RemindTime")
    private LocalDate remindTime;
    @Column(name = "Status")
    private int status;

    public Card(){
        this.remindTime = LocalDate.now();
    }

    public int getCardID() {
        return cardID;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    public int getDeckID() {
        return deckID;
    }

    public void setDeckID(int deckID) {
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

    public LocalDate getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(LocalDate remindTime) {
        this.remindTime = remindTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}