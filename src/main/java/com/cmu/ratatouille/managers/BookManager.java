package com.cmu.ratatouille.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.models.Book;
import com.cmu.ratatouille.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookManager extends Manager {
    public static BookManager _self;
    private MongoCollection<Document> bookCollection;


    public BookManager() {
        this.bookCollection = MongoPool.getInstance().getCollection("Books");
    }

    public static BookManager getInstance(){
        if (_self == null)
            _self = new BookManager();
        return _self;
    }
    public void createBook(Book book) throws AppException {
        try{
            JSONObject json = new JSONObject(book);

            Document newDoc = new Document()
                    .append("book_id", book.getBook_id())
                    .append("book_name", book.getBook_name())
                    .append("book_author",book.getBook_author())
                    .append("book_status",book.getBook_status());
            if (newDoc != null)
                bookCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new book");

        }catch(Exception e){
            throw handleException("Create Book", e);
        }

    }
    public void updateBook( Book book) throws AppException {
        try {
            Bson filter = new Document("book_id", book.getBook_id());
            Bson newValue = new Document()
                    .append("book_name", book.getBook_name())
                    .append("book_author",book.getBook_author())
                    .append("book_status",book.getBook_status());
            Bson updateOperationDocument = new Document("$set", newValue);
            if (newValue != null)
                bookCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update book details");
        } catch(Exception e) {
            throw handleException("Update Book", e);
        }
    }
    public void deleteBook(String bookId) throws AppException {
        try {
            Bson filter = new Document("book_id",bookId);
            bookCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete Book", e);
        }
    }
    public ArrayList<Book> getBookList() throws AppException {
        try{
            ArrayList<Book> bookList = new ArrayList<>();
            FindIterable<Document> bookDocs = bookCollection.find();
            for(Document bookDoc: bookDocs) {
                Book book = new Book(
                        bookDoc.getObjectId("_id").toString(),
                        bookDoc.getString("book_id"),
                        bookDoc.getString("book_name"),
                        bookDoc.getString("book_author"),
                        bookDoc.getString("book_status")
                );
                bookList.add(book);
            }
            return new ArrayList<>(bookList);
        } catch(Exception e){
            throw handleException("Get Book List", e);
        }
    }
    public ArrayList<Book> getBookById(String id) throws AppException {
        try{
            ArrayList<Book> bookList = new ArrayList<>();
            FindIterable<Document> bookDocs = bookCollection.find();
            for(Document bookDoc: bookDocs) {
                if(bookDoc.getString("book_id").equals(id)) {
                    Book book = new Book(
                            bookDoc.getObjectId("_id").toString(),
                            bookDoc.getString("book_id"),
                            bookDoc.getString("book_name"),
                            bookDoc.getString("book_author"),
                            bookDoc.getString("book_status")
                    );
                    bookList.add(book);
                }
            }
            return new ArrayList<>(bookList);
        } catch(Exception e){
            throw handleException("Get Book List", e);
        }
    }
}
