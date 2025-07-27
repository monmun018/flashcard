package com.app.flashcard.forms;


import jakarta.validation.constraints.NotEmpty;

public class LoginForm {
    @NotEmpty(message = "Tài khoản không hợp lệ")
    private String loginID;
    @NotEmpty(message = "Mật khẩu không hợp lệ")
    private String pw;

    public  LoginForm(){}

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }
}
