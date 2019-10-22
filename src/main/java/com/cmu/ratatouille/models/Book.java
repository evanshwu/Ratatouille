package com.cmu.ratatouille.models;

public class Book {
    String id = null;
    String book_id = null;
    String book_name = null;
    String book_author = null;
    String book_status = null;

    public Book(String id, String book_id, String book_name, String book_author, String book_status) {
        this.id = id;
        this.book_id = book_id;
        this.book_name = book_name;
        this.book_author = book_author;
        this.book_status = book_status;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public String getBook_id() {
        return book_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public String getBook_author() {
        return book_author;
    }

    public String getBook_status() {
        return book_status;
    }
}