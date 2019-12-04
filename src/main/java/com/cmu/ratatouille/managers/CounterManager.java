package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.models.Counter;
import com.cmu.ratatouille.utils.MongoPool;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

public class CounterManager extends Manager {
    public static CounterManager _self;
    private MongoCollection<Document> counterCollection;

    public CounterManager() {
        this.counterCollection = MongoPool.getInstance().getCollection("Counter");
    }

    public static CounterManager getInstance(){
        if (_self == null)
            _self = new CounterManager();
        return _self;
    }

    public void setRecipeCount(int newIndex) throws AppException{
        try{
            // Get counter first
            Counter counter = this.getCounter();
            counter.setRecipeId(newIndex);
            Document newCounter = Document.parse(new Gson().toJson(counter));
            Bson filter = new Document("func", "counter");
            Bson updateOperationDocument = new Document("$set", newCounter);

            if (newCounter != null)
                counterCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update counter");
        }catch (Exception e){
            throw handleException("Error setting recipe ID with new index!", e);
        }
    }

    public Counter getCounter() throws AppException {
        try{
            FindIterable<Document> counterDocs = counterCollection.find();
            Counter counter = new Gson().fromJson(counterDocs.first().toJson(), Counter.class);
            return counter;
        }catch(Exception e){
            throw handleException("Error getting counter object!", e);
        }
    }

    public int pushCount() throws AppException{
        try{
            Counter counter = getCounter();
            setRecipeCount(counter.getRecipeId()+1);
            return counter.getRecipeId()+1;
        }catch (Exception ex){
            throw handleException("Error adding counter value!", ex);
        }
    }
}
