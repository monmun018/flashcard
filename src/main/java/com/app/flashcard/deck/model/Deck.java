package com.app.flashcard.deck.model;

import jakarta.persistence.*;

@Entity
@Table(name = "deck")
public class Deck {
//    | UserID          | varchar(10) | NO   |     | NULL    |       |
//    | DeckID          | varchar(10) | NO   | PRI | NULL    |       |
//    | DeckName        | varchar(50) | YES  |     | NULL    |       |
//    | NewCardNum      | int(10)     | YES  |     | NULL    |       |
//    | LearningCardNum | int(10)     | YES  |     | NULL    |       |
//    | DueCardNum      | int(10)     | YES  |     | NULL    |       |
    private int userID;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int deckID;
    private String deckName;
    private int newCardNum = 0;
    private int learningCardNum = 0;
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