package com.app.flashcard.controllers;

import com.app.flashcard.forms.LoginForm;
import com.app.flashcard.models.User;

public class Session {

    private User user = null;
    private boolean isLogin;

    private int cardID;
    private int deckID;
    public Session(){}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public int getCardID() {
        return cardID;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    public int getDeckID() {
        return deckID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
    }
}
