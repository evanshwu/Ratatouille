package com.cmu.ratatouille.models;

public class Message {
    public String timestamp;
    public String context;

    public Message(String context) {
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.context = context;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
