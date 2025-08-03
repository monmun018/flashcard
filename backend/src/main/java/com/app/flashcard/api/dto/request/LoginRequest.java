package com.app.flashcard.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    
    @NotBlank(message = "Login ID is required")
    @Size(max = 50, message = "Login ID must not exceed 50 characters")
    private String loginId;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;
    
    public LoginRequest() {}
    
    public LoginRequest(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
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
}