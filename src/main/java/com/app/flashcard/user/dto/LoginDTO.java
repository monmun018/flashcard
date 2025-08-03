package com.app.flashcard.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user login
 */
public class LoginDTO {
    
    @NotBlank(message = "Tài khoản không được để trống")
    @Size(min = 3, max = 50, message = "Tài khoản phải từ 3-50 ký tự")
    private String loginID;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 4, max = 100, message = "Mật khẩu phải từ 4-100 ký tự")
    private String password;

    public LoginDTO() {}

    public LoginDTO(String loginID, String password) {
        this.loginID = loginID;
        this.password = password;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}