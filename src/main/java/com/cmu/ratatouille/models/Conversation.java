package com.cmu.ratatouille.models;

import java.util.ArrayList;

public class Conversation {
    public String id;
    public String userId;
    public ArrayList<Message> messages;

    public Conversation(String id, String userId, ArrayList<Message> messages) {
        this.id = id;
        this.userId = userId;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
