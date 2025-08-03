package com.app.flashcard.api.dto.response;

import java.time.LocalDate;

public class CardResponse {
    
    private Integer id;
    private Integer deckId;
    private String frontContent;
    private String backContent;
    private LocalDate remindTime;
    private Integer status;
    
    public CardResponse() {}
    
    public CardResponse(Integer id, Integer deckId, String frontContent, String backContent, 
                       LocalDate remindTime, Integer status) {
        this.id = id;
        this.deckId = deckId;
        this.frontContent = frontContent;
        this.backContent = backContent;
        this.remindTime = remindTime;
        this.status = status;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
    
    public LocalDate getRemindTime() {
        return remindTime;
    }
    
    public void setRemindTime(LocalDate remindTime) {
        this.remindTime = remindTime;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
}