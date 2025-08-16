package com.app.flashcard.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
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

    public User setByUserData(String loginID, String password, String name, int age, String email){
            this.userLoginID = loginID;
            this.userPW = password;
            this.userName = name;
            this.userAge = age;
            this.userMail = email;
        return this;
    }
}