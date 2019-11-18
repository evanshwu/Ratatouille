package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.http.utils.PATCH;
import com.cmu.ratatouille.managers.FavoriteManager;
import com.cmu.ratatouille.managers.RecipeManager;
import com.cmu.ratatouille.managers.WishListManager;
import com.cmu.ratatouille.models.FavoriteList;
import com.cmu.ratatouille.models.Recipe;
import com.cmu.ratatouille.models.WishList;
import com.cmu.ratatouille.utils.AppLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/wishlist")
public class WishListHttpInterface extends HttpInterface {
    private ObjectWriter ow;
    private MongoCollection<Document> wishlistCollection = null;

    public WishListHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postWishList(Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            WishList wishList = new Gson().fromJson(json.toString(), WishList.class);
            WishListManager.getInstance().createWishList(wishList);
            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST recipes", e);
        }
    }
    @DELETE
    @Path("/{recipeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteWishList(@PathParam("recipeId") String recipeId){
        try{
            WishListManager.getInstance().deleteWishList(recipeId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE wishlist/{recipeId}", e);
        }
    }

}
