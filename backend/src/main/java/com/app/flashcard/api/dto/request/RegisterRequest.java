package com.app.flashcard.api.dto.request;

import jakarta.validation.constraints.*;

public class RegisterRequest {
    
    @NotBlank(message = "Login ID is required")
    @Size(max = 50, message = "Login ID must not exceed 50 characters")
    private String loginId;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 150, message = "Age must not exceed 150")
    private Integer age;
    
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    public RegisterRequest() {}
    
    public RegisterRequest(String loginId, String password, String name, Integer age, String email) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.age = age;
        this.email = email;
    }
    
    public String getLoginId() {
        return loginId;
    }
    
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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