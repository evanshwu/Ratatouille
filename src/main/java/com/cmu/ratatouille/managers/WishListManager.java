package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.models.WishList;
import com.cmu.ratatouille.utils.MongoPool;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

public class WishListManager extends Manager {
    public static WishListManager _self;
    private MongoCollection<Document> wishlistCollection;

    public WishListManager() {
        this.wishlistCollection = MongoPool.getInstance().getCollection("wishlists");
    }

    public static WishListManager getInstance(){
        if (_self == null)
            _self = new WishListManager();
        return _self;
    }

    public void createWishList(WishList wishlist) throws AppException {
        try {
            Gson gson = new Gson();
            Document newDoc = Document.parse(gson.toJson(wishlist));
            if (newDoc != null)
                wishlistCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new wishlist");

        } catch (Exception e) {
            throw handleException("Create wishlist", e);
        }

    }

    public void deleteWishList(String recipename) throws AppException {
        try {
            Bson filter = new Document("recipeName", recipename);
            wishlistCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete wishlist", e);
        }
    }

}

