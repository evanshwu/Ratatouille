package com.cmu.ratatouille.models;

import java.util.UUID;

public class Session {

    public String token;
    public String userId;

    public Session(User user) throws Exception{
        this.userId = user.userId;
        //this.token = APPCrypt.encrypt(user.id);
        this.token = UUID.randomUUID().toString();
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
