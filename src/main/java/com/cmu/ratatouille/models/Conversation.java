package com.cmu.ratatouille.models;

import java.util.ArrayList;

public class Conversation {
    public _id _id;
    public String userName;
    public ArrayList<Message> messages;

    public Conversation(String _id, String userName, ArrayList<Message> messages) {
        this.userName = userName;
        this.messages = messages;
    }

    public String get_id() {
        return _id.get$Oid();
    }

    public void set_id(String _id) {
        _id _id_ = new _id();
        _id_.set$Oid(_id);
        this._id = _id_;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
