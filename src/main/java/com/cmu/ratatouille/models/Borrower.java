package com.cmu.ratatouille.models;

public class Borrower {
    String id = null;
    String borrower_id = null;
    String borrower_phone = null;
    String borrower_name = null;
    public Borrower(String id, String borrower_id, String borrower_phone, String borrower_name) {
        this.id = id;
        this.borrower_id = borrower_id;
        this.borrower_name = borrower_name;
        this.borrower_phone = borrower_phone;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public String getBorrower_id() {
        return borrower_id;
    }
    public String getBorrower_name() {
        return borrower_name;
    }
    public String getBorrower_phone() {
        return borrower_phone;
    }
}
