package com.cmu.ratatouille.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.managers.BookManager;
import com.cmu.ratatouille.managers.BorrowerManager;
import com.cmu.ratatouille.models.Book;
import com.cmu.ratatouille.models.Borrower;
import com.cmu.ratatouille.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/checkouts")
public class CheckoutHttpInterface extends HttpInterface {
    private ObjectWriter ow;
    private MongoCollection<Document> borrowerCollection = null;
    private MongoCollection<Document> bookCollection = null;
    public CheckoutHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getCheckoutBooks(@Context HttpHeaders headers) {
        try {
            AppLogger.info("Got an API call");
            ArrayList<Book> books = BookManager.getInstance().getBookList();
            ArrayList<Book> new_ss = new ArrayList<Book>();
            for (Book my_book : books) {
                if (my_book.getBook_status().equals("false")) {
                    new_ss.add(my_book);
                }
            }
            if (new_ss != null)
                return new AppResponse(new_ss);
            else
                throw new HttpBadRequestException(0, "Problem with getting books");
        } catch (Exception e) {
            throw handleException("GET /checkouts", e);
        }
    }
    @POST
    @Path("/checkouts")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse CheckoutBooks(Object request, @QueryParam("checkouts") String checkouts, @QueryParam("bookId") String bookId, @QueryParam("borrowerId") String borrowerId){
        JSONObject json = null;
        try{
            json = new JSONObject(ow.writeValueAsString(request));
            Book book = new Book(
                    null,
                    json.getString("book_id"),
                    json.getString("book_name"),
                    json.getString("book_author"),
                    json.getString("book_status")
            );
            Borrower borrower = new Borrower(
                    null,
                    json.getString("borrower_id"),
                    json.getString("borrower_phone"),
                    json.getString("borrower_name")
            );
            BookManager.getInstance().updateBook(book);
            BorrowerManager.getInstance().updateBorrower(borrower);
        }catch (Exception e){
            throw handleException("POST books/checkouts?bookId={bookId},borrowerId={borrowerId}", e);
        }
        return new AppResponse("Checkout Successful");
    }
    @DELETE
    @Path("/checkouts")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse ReturnBooks(Object request, @QueryParam("checkouts") String checkouts, @QueryParam("bookId") String bookId, @QueryParam("borrowerId") String borrowerId){
        JSONObject json = null;
        try{
            json = new JSONObject(ow.writeValueAsString(request));
            Book book = new Book(
                    null,
                    json.getString("book_id"),
                    json.getString("book_name"),
                    json.getString("book_author"),
                    json.getString("book_status")
            );
            Borrower borrower = new Borrower(
                    null,
                    json.getString("borrower_id"),
                    json.getString("borrower_phone"),
                    json.getString("borrower_name")
            );
            BookManager.getInstance().updateBook(book);
            BorrowerManager.getInstance().updateBorrower(borrower);
        }catch (Exception e){
            throw handleException("DELETE books/checkouts?bookId={bookId},borrowerId={borrowerId}", e);
        }
        return new AppResponse("Return Successful");
    }
}
