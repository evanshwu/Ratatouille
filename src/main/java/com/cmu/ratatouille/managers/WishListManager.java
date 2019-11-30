package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.models.Recipe;
import com.cmu.ratatouille.models.User;
import com.cmu.ratatouille.models.WishList;
import com.cmu.ratatouille.utils.MongoPool;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import javax.print.Doc;
import java.util.ArrayList;

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
        try{
            JSONObject json = new JSONObject(wishlist);
            Gson gson = new Gson();
            Document newDoc = Document.parse(gson.toJson(wishlist));
            if (newDoc != null)
                wishlistCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new wishlist");

        }catch(Exception e){
            throw handleException("Create wishlist", e);
        }

    }

    public void updateWishList(Recipe recipe) throws AppException {
        try {
            Bson filter = new Document("recipeName", recipe.getRecipeName());
            Gson gson =new Gson();
            Bson newValue = Document.parse(gson.toJson(recipe));
            Bson updateOperationDocument = new Document("$set", newValue);
            if (newValue != null)
                wishlistCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update wishlist");

        } catch(Exception e) {
            throw handleException("Update Wishlist", e);
        }
    }

    public void deleteWishList(String recipename) throws AppException {
        try {
            Bson filter = new Document("recipeName", recipename);
            wishlistCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete recipe", e);
        }
    }

    public ArrayList<WishList> getWishList() throws AppException {
        try{
            ArrayList<WishList> wishlist = new ArrayList<>();
            Gson gson = new Gson();
            Bson newValue = Document.parse(gson.toJson(wishlist));

            FindIterable<Document> wishlistDocs = wishlistCollection.find();
            for(Document wishlistDoc: wishlistDocs) {
                WishList _wishlist = gson.fromJson(wishlistDoc.toJson(), WishList.class);
                wishlist.add(_wishlist);
            }
            return new ArrayList<>(wishlist);
        } catch(Exception e){
            throw handleException("Get Wish List", e);
        }
    }
}

