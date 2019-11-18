package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.http.utils.PATCH;
import com.cmu.ratatouille.managers.FavoriteManager;
import com.cmu.ratatouille.managers.Manager;
import com.cmu.ratatouille.managers.UserManager;
import com.cmu.ratatouille.managers.WishListManager;
import com.cmu.ratatouille.models.FavoriteList;
import com.cmu.ratatouille.models.User;
import com.cmu.ratatouille.models.WishList;
import com.cmu.ratatouille.utils.AppLogger;
import com.cmu.ratatouille.utils.MongoPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/favoritelist")
public class FavoriteHttpInterface extends HttpInterface {
    private ObjectWriter ow;
    private MongoCollection<Document> favoriteCollection = null;

    public FavoriteHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postFavoriteList(Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            FavoriteList favoriteList = new Gson().fromJson(json.toString(), FavoriteList.class);
            FavoriteManager.getInstance().createFavoriteList(favoriteList);
            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST favorite lists", e);
        }
    }
    @DELETE
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteFavoriteList(@QueryParam("favoriteId") String favoriteId){
        try{
            FavoriteManager.getInstance().deleteFavoriteList(favoriteId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE favorite list/{favoriteId}", e);
        }
    }
    @PATCH
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchFavoriteLists(Object request, @QueryParam("favoriteId") String favoriteId){
        JSONObject json = null;
        try{
            json = new JSONObject(ow.writeValueAsString(request));
            FavoriteList favoriteList = new Gson().fromJson(json.toString(), FavoriteList.class);
            FavoriteManager.getInstance().updateFavoriteList(favoriteList);

        }catch (Exception e){
            throw handleException("PATCH users/{username}", e);
        }

        return new AppResponse("Update Successful");
    }
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getsortFavoriteList(Object request, @Context HttpHeaders headers,
                                   @QueryParam("sortby") String sortBy,
                                   @QueryParam("pagesize") Integer pageSize,
                                   @QueryParam("page") Integer page,
                                   @QueryParam("favoriteId") Integer favoriteId) {
        AppLogger.info("[getsortFavoriteList]" + sortBy);
        if (sortBy == null && pageSize == null && page == null) {
            try {
                AppLogger.info("Got an API call");
                ArrayList<FavoriteList> favoriteLists = FavoriteManager.getInstance().getFavoriteList();

                if (favoriteLists != null)
                    return new AppResponse(favoriteLists);
                else
                    throw new HttpBadRequestException(0, "Problem with getting favoritelists");
            } catch (Exception e) {
                throw handleException("GET /favoritelists", e);
            }
        } else if (sortBy != null && page == null && pageSize == null) {

            try {
                AppLogger.info("hello");
                JSONObject json = null;
                FindIterable<Document> favoriteDocs = MongoPool.getInstance().getCollection("favoritelists").find().sort(Sorts.ascending(sortBy));
                AppLogger.info("size=" + favoriteDocs.toString());
                FavoriteList favoriteList=null;
                for (Document favoriteDoc : favoriteDocs) {
                    AppLogger.info("Got a doc");
                    json = new JSONObject(ow.writeValueAsString(request));
                    Gson gson = new Gson();
                    favoriteList = new Gson().fromJson(json.toString(), FavoriteList.class);
                }
                if (favoriteList != null)
                    return new AppResponse(favoriteList);
                else
                    throw new HttpBadRequestException(0, "Problem with getting favoritelists");

            } catch (Exception e) {
                throw handleException("GET /users?sortby={username}", e);
            }
        } else if(sortBy==null&&page!=null&&pageSize!=null){
            try {
                AppLogger.info("hello");
                JSONObject json = null;
                FindIterable<Document> favoriteDocs = MongoPool.getInstance().getCollection("favoritelists").find().skip(pageSize * (page - 1)).limit(pageSize);
                AppLogger.info("size=" + favoriteDocs.toString());
                FavoriteList favoriteList=null;
                for (Document favoriteDoc : favoriteDocs) {
                    AppLogger.info("Got a doc");
                    json = new JSONObject(ow.writeValueAsString(request));
                    Gson gson = new Gson();
                    favoriteList = new Gson().fromJson(json.toString(), FavoriteList.class);
                }
                if (favoriteList != null)
                    return new AppResponse(favoriteList);
                else
                    throw new HttpBadRequestException(0, "Problem with getting favoritelists");

            } catch (Exception e) {
                throw handleException("GET /users?sortby={favoriteId}", e);
            }
        }
        else{
            try {
                AppLogger.info("Got an API call");
                ArrayList<FavoriteList> favoriteLists = FavoriteManager.getInstance().getFavoriteById(favoriteId);

                if (favoriteLists != null)
                    return new AppResponse(favoriteLists);
                else
                    throw new HttpBadRequestException(0, "Problem with getting favoriteLists");
            } catch (Exception e) {
                throw handleException("GET /favoriteLists/{favoriteId}", e);
            }
        }
    }

}
