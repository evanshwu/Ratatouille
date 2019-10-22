package com.cmu.ratatouille.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.http.utils.PATCH;
import com.cmu.ratatouille.managers.BookManager;
import com.cmu.ratatouille.models.Book;
import com.cmu.ratatouille.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/books")
public class BookHttpInterface extends HttpInterface {
    private ObjectWriter ow;
    private MongoCollection<Document> bookCollection = null;
    public BookHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postBooks(Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Book newbook = new Book(
                    null,
                    json.getString("book_id"),
                    json.getString("book_name"),
                    json.getString("book_author"),
                    json.getString("book_status")
            );
            BookManager.getInstance().createBook(newbook);
            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST books", e);
        }
    }
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getBooks(@Context HttpHeaders headers){
        try{
            AppLogger.info("Got an API call");
            ArrayList<Book> books = BookManager.getInstance().getBookList();

            if(books != null)
                return new AppResponse(books);
            else
                throw new HttpBadRequestException(0, "Problem with getting books");
        }catch (Exception e){
            throw handleException("GET /books", e);
        }
    }
    @GET
    @Path("/books")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse gettrueBook(@Context HttpHeaders headers, @QueryParam("available") String available){
        try{
            AppLogger.info("Got an API call");
            ArrayList<Book> books = BookManager.getInstance().getBookList();
            ArrayList<Book> new_s = new ArrayList<Book>();
            for(Book my_book:books)
            {
                if(my_book.getBook_status().equals("true"))
                {
                    new_s.add(my_book);
                }
            }
            if(new_s!= null)
                return new AppResponse(new_s);
            else
                throw new HttpBadRequestException(0, "Problem with getting books");
        }catch (Exception e){
            throw handleException("GET /books?available=true", e);
        }
    }
    @GET
    @Path("/{bookId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleBook(@Context HttpHeaders headers, @PathParam("bookId") String bookId){
        try{
            AppLogger.info("Got an API call");
            ArrayList<Book> books = BookManager.getInstance().getBookById(bookId);
            if(books != null)
                return new AppResponse(books);
            else
                throw new HttpBadRequestException(0, "Problem with getting books");
        }catch (Exception e){
            throw handleException("GET /books/{bookId}", e);
        }
    }
    @PATCH
    @Path("/{bookId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchBooks(Object request, @PathParam("bookId") String bookId){
        try{
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Book book = new Book(
                    null,
                    json.getString("book_id"),
                    json.getString("book_name"),
                    json.getString("book_author"),
                    json.getString("book_status")
            );
            BookManager.getInstance().updateBook(book);
        }catch (Exception e){
            throw handleException("PATCH books/{bookId}", e);
        }
        return new AppResponse("Update Successful");
    }
    @DELETE
    @Path("/{bookId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteBooks(@PathParam("bookId") String bookId){
        try{
            BookManager.getInstance().deleteBook(bookId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE books/{bookId}", e);
        }
    }
}
