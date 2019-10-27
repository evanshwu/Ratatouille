package com.cmu.ratatouille.managers;

import com.mongodb.client.MongoCollection;
import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.utils.AppLogger;
import com.cmu.ratatouille.utils.MongoPool;
import org.bson.Document;


public class Manager {
    protected MongoCollection<Document> userCollection;
    protected MongoCollection<Document> sessionCollection;

    public Manager() {
        this.userCollection = MongoPool.getInstance().getCollection("users");
    }

    protected AppException handleException(String message, Exception e){
        if (e instanceof AppException && !(e instanceof AppInternalServerException))
            return (AppException)e;
        AppLogger.error(message, e);
        return new AppInternalServerException(-1);
    }
}
