package com.cmu.ratatouille.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.models.Borrower;
import com.cmu.ratatouille.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.ArrayList;

public class BorrowerManager extends Manager {
    public static BorrowerManager _self;
    private MongoCollection<Document> borrowerCollection;
    public BorrowerManager() {
        this.borrowerCollection = MongoPool.getInstance().getCollection("Borrowers");
    }
    public static BorrowerManager getInstance(){
        if (_self == null)
            _self = new BorrowerManager();
        return _self;
    }
    public void createBorrower(Borrower borrower) throws AppException {
        try{
            JSONObject json = new JSONObject(borrower);
            Document newDoc = new Document()
                    .append("borrower_id", borrower.getBorrower_id())
                    .append("borrower_name", borrower.getBorrower_name())
                    .append("borrower_phone",borrower.getBorrower_phone());
            if (newDoc != null)
                borrowerCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new borrower");
        }catch(Exception e){
            throw handleException("Create Borrower", e);
        }
    }
    public void updateBorrower( Borrower borrower) throws AppException {
        try {
            Bson filter = new Document("borrower_id", borrower.getId());
            Bson newValue = new Document()
                    .append("borrower_name", borrower.getBorrower_name())
                    .append("borrower_phone",borrower.getBorrower_phone());
            Bson updateOperationDocument = new Document("$set", newValue);
            if (newValue != null)
                borrowerCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update borrower details");
        } catch(Exception e) {
            throw handleException("Update Borrower", e);
        }
    }
    public void deleteBorrower(String borrowerId) throws AppException {
        try {
            Bson filter = new Document("borrower_id",borrowerId);
            borrowerCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete Borrower", e);
        }
    }
    public ArrayList<Borrower> getBorrowerList() throws AppException {
        try{
            ArrayList<Borrower> borrowerList = new ArrayList<>();
            FindIterable<Document> borrowerDocs = borrowerCollection.find();
            for(Document borrowerDoc: borrowerDocs) {
                Borrower borrower = new Borrower(
                        borrowerDoc.getObjectId("_id").toString(),
                        borrowerDoc.getString("borrower_id"),
                        borrowerDoc.getString("borrower_name"),
                        borrowerDoc.getString("borrower_phone")
                );
                borrowerList.add(borrower);
            }
            return new ArrayList<>(borrowerList);
        } catch(Exception e){
            throw handleException("Get Borrower List", e);
        }
    }
    public ArrayList<Borrower> getBorrowerById(String id) throws AppException {
        try{
            ArrayList<Borrower> borrowerList = new ArrayList<>();
            FindIterable<Document> borrowerDocs = borrowerCollection.find();
            for(Document borrowerDoc: borrowerDocs) {
                if(borrowerDoc.getString("borrower_id").equals(id)) {
                    Borrower borrower = new Borrower(
                            borrowerDoc.getObjectId("_id").toString(),
                            borrowerDoc.getString("borrower_id"),
                            borrowerDoc.getString("borrower_name"),
                            borrowerDoc.getString("borrower_phone")
                    );
                    borrowerList.add(borrower);
                }
            }
            return new ArrayList<>(borrowerList);
        } catch(Exception e){
            throw handleException("Get Borrower List", e);
        }
    }
}

