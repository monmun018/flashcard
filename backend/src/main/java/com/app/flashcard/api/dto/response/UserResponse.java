package com.app.flashcard.api.dto.response;

public class UserResponse {
    
    private Integer id;
    private String loginId;
    private String name;
    private Integer age;
    private String email;
    
    public UserResponse() {}
    
    public UserResponse(Integer id, String loginId, String name, Integer age, String email) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.age = age;
        this.email = email;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getLoginId() {
        return loginId;
    }
    
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}