package com.app.flashcard.models;

import com.app.flashcard.forms.RegistForm;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private int userID;
    @Column(name = "UserLoginID")
    private String userLoginID;
    @Column(name = "UserPW")
    private String userPW;
    @Column(name = "UserName")
    private String userName;
    @Column(name = "UserAge")
    private int userAge;
    @Column(name = "UserMail")
    private String userMail;

    public User(){}
    public User(String userLoginID, String userPW, String userName, int userAge, String userMail) {
        this.userLoginID = userLoginID;
        this.userPW = userPW;
        this.userName = userName;
        this.userAge = userAge;
        this.userMail = userMail;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserLoginID() {
        return userLoginID;
    }

    public void setUserLoginID(String userLoginID) {
        this.userLoginID = userLoginID;
    }

    public String getUserPW() {
        return userPW;
    }

    public void setUserPW(String userPW) {
        this.userPW = userPW;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public User setByRegistForm(RegistForm form){
            this.userLoginID = form.getLoginID();
            this.userPW = form.getPw();
            this.userName = form.getName();
            this.userAge = form.getAge();
            this.userMail = form.getMail();
        return this;
    }
}
