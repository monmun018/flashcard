package com.app.flashcard.forms;

import javax.validation.constraints.*;

public class RegistForm {
    private String loginID;
    @NotBlank
    private String pw;
    @NotBlank
    private String name;
    @NotNull
    private int age;
    @NotBlank
    @Email
    private String mail;

    public RegistForm(){}
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
