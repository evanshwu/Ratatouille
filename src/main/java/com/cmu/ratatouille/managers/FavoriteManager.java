package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.models.FavoriteList;
import com.cmu.ratatouille.utils.MongoPool;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoriteManager extends Manager {
    public static FavoriteManager _self;
    private MongoCollection<Document> favoriteCollection;

    public FavoriteManager() {
        this.favoriteCollection = MongoPool.getInstance().getCollection("favoritelists");
    }

    public static FavoriteManager getInstance(){
        if (_self == null)
            _self = new FavoriteManager();
        return _self;
    }
    public void createFavoriteList(FavoriteList favoriteList) throws AppException {

        try{
            JSONObject json = new JSONObject(favoriteList);
            Gson gson = new Gson();
            Document newDoc = Document.parse(gson.toJson(favoriteList));
            if (newDoc != null)
                favoriteCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new favoriteList");

        }catch(Exception e){
            throw handleException("Create favoriteList", e);
        }
    }
    public void updateFavoriteList(FavoriteList favoriteList) throws AppException {
        try {
            Bson filter = new Document("favoriteId", favoriteList.getFavoriteId());
            Gson gson =new Gson();
            Bson newValue = Document.parse(gson.toJson(favoriteList));
            Bson updateOperationDocument = new Document("$set", newValue);
            if (newValue != null)
                favoriteCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update Favoritelist");

        } catch(Exception e) {
            throw handleException("Update Favoritelist", e);
        }
    }
    public void deleteFavoriteList(String favoriteId) throws AppException {
        try {
            Bson filter = new Document("favoriteId", favoriteId);
            favoriteCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete favoritelist", e);
        }
    }
    public ArrayList<FavoriteList> getFavoriteList() throws AppException {
        try{
            ArrayList<FavoriteList> favoritelist = new ArrayList<>();

            FindIterable<Document> favoritelistDocs = favoriteCollection.find();
            for(Document favoritelistDoc: favoritelistDocs) {
                FavoriteList _favoriteList = new Gson().fromJson(favoritelistDoc.toJson(),FavoriteList.class);
                favoritelist.add(_favoriteList);
            }
            return new ArrayList<>(favoritelist);
        } catch(Exception e){
            throw handleException("Get Favorite List", e);
        }
    }
    public ArrayList<FavoriteList> getFavoriteById(Integer favoriteId) throws AppException {
        try{
            ArrayList<FavoriteList> favoriteList = new ArrayList<>();
            FindIterable<Document> favoriteDocs = favoriteCollection.find();
            Gson gson = new Gson();
            for(Document favoriteDoc: favoriteDocs) {
                if(favoriteDoc.getString("favoriteId").equals(favoriteId)) {
                    FavoriteList _favoriteList = gson.fromJson(favoriteDoc.toJson(),FavoriteList.class);
                    favoriteList.add(_favoriteList);
                }
            }
            return new ArrayList<>(favoriteList);
        } catch(Exception e){
            throw handleException("Get Favorite List", e);
        }
    }


}
