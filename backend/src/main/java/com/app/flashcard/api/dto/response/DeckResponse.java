package com.app.flashcard.api.dto.response;

public class DeckResponse {
    
    private Integer id;
    private Integer userId;
    private String name;
    private Integer newCardNum;
    private Integer learningCardNum;
    private Integer dueCardNum;
    
    public DeckResponse() {}
    
    public DeckResponse(Integer id, Integer userId, String name, Integer newCardNum, 
                       Integer learningCardNum, Integer dueCardNum) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.newCardNum = newCardNum;
        this.learningCardNum = learningCardNum;
        this.dueCardNum = dueCardNum;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getNewCardNum() {
        return newCardNum;
    }
    
    public void setNewCardNum(Integer newCardNum) {
        this.newCardNum = newCardNum;
    }
    
    public Integer getLearningCardNum() {
        return learningCardNum;
    }
    
    public void setLearningCardNum(Integer learningCardNum) {
        this.learningCardNum = learningCardNum;
    }
    
    public Integer getDueCardNum() {
        return dueCardNum;
    }
    
    public void setDueCardNum(Integer dueCardNum) {
        this.dueCardNum = dueCardNum;
    }
}