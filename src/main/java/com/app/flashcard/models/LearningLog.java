package com.app.flashcard.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "learningLog")
public class LearningLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logID;
    private int deckID;
    private int userID;
    private int learnTime;
    private LocalDate logTime;

    public LearningLog(){
        this.logTime = LocalDate.now();
        this.learnTime = 1;
    }

    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public int getDeckID() {
        return deckID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public LocalDate getLogTime() {
        return logTime;
    }

    public void setLogTime(LocalDate logTime) {
        this.logTime = logTime;
    }

    public int getLearnTime() {
        return learnTime;
    }

    public void setLearnTime(int learnTime) {
        this.learnTime = learnTime;
    }

    public void increaseLearnTime(){
        this.learnTime +=1;
    }
}
