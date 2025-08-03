package com.app.flashcard.deck.model;

import jakarta.persistence.*;

@Entity
@Table(name = "deck")
public class Deck {
    @Column(name = "UserID")
    private int userID;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DeckID")
    private int deckID;
    @Column(name = "DeckName")
    private String deckName;
    @Column(name = "NewCardNum")
    private int newCardNum = 0;
    @Column(name = "LearningCardNum")
    private int learningCardNum = 0;
    @Column(name = "DueCardNum")
    private int dueCardNum = 0;

    public Deck(){}

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getDeckID() {
        return deckID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public int getNewCardNum() {
        return newCardNum;
    }

    public void setNewCardNum(int newCardNum) {
        this.newCardNum = newCardNum;
    }

    public int getLearningCardNum() {
        return learningCardNum;
    }

    public void setLearningCardNum(int learningCardNum) {
        this.learningCardNum = learningCardNum;
    }

    public int getDueCardNum() {
        return dueCardNum;
    }

    public void setDueCardNum(int dueCardNum) {
        this.dueCardNum = dueCardNum;
    }
}