package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.http.utils.StringUtil;
import com.cmu.ratatouille.managers.BookManager;
import com.cmu.ratatouille.managers.FavoriteManager;
import com.cmu.ratatouille.managers.RecipeManager;
import com.cmu.ratatouille.models.FavoriteList;
import com.cmu.ratatouille.models.Recipe;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.http.utils.PATCH;
import com.cmu.ratatouille.managers.UserManager;
import com.cmu.ratatouille.models.User;
import com.cmu.ratatouille.utils.*;
import com.mongodb.client.model.Sorts;
import jdk.internal.jline.internal.Nullable;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.validation.constraints.Null;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

@Path("/users")

 public class UserHttpInterface extends HttpInterface {

    private ObjectWriter ow;
    private MongoCollection<Document> userCollection = null;

    public UserHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postUsers(Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            User user = new Gson().fromJson(json.toString(), User.class);
            UserManager.getInstance().createUser(user);

            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST recipes", e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getsortUser(@Context HttpHeaders headers,
                                   @QueryParam("sortby") String sortBy,
                                   @QueryParam("pagesize") Integer pageSize,
                                   @QueryParam("page") Integer page,
                                   @QueryParam("name") String name) {
        AppLogger.info("[getsortUser]" + sortBy);
        if (sortBy == null && pageSize == null && page == null&&name==null) {
            try {
                JSONObject json = null;
                AppLogger.info("Got an API call");
                ArrayList<User> users = UserManager.getInstance().getUserList();

                if (users != null)
                    return new AppResponse(users);
                else
                    throw new HttpBadRequestException(0, "Problem with getting users");
            } catch (Exception e) {
                throw handleException("GET /users", e);
            }
        } else if (sortBy != null && page == null && pageSize == null) {

            try {
                JSONObject json = null;
                AppLogger.info("hello");
                FindIterable<Document> userDocs = MongoPool.getInstance().getCollection("users").find().sort(Sorts.ascending(sortBy));
                AppLogger.info("size=" + userDocs.toString());
                ArrayList<User> userList = new ArrayList<>();
                for (Document userDoc : userDocs) {
                    AppLogger.info("Got a doc");
                    User _userList = new Gson().fromJson(userDoc.toJson(),User.class);
                    userList.add(_userList);
                }
                if (userList != null)
                    return new AppResponse(userList);
                else
                    throw new HttpBadRequestException(0, "Problem with getting users");

            } catch (Exception e) {
                throw handleException("GET /users?sortby={username}", e);
            }
        } else if(sortBy==null&&page!=null&&pageSize!=null){
            try {
                AppLogger.info("hello");
                FindIterable<Document> userDocs = MongoPool.getInstance().getCollection("users").find().skip(pageSize * (page - 1)).limit(pageSize);
                AppLogger.info("size=" + userDocs.toString());
                ArrayList<User> userList = new ArrayList<>();
                for (Document userDoc : userDocs) {
                    AppLogger.info("Got a doc");
                    User _userList = new Gson().fromJson(userDoc.toJson(),User.class);
                    userList.add(_userList);
                }
                if (userList != null)
                    return new AppResponse(userList);
                else
                    throw new HttpBadRequestException(0, "Problem with getting users");

            } catch (Exception e) {
                throw handleException("GET /users?sortby={username}", e);
            }
        }
        else{
            try {
                AppLogger.info("Got an API call");
                ArrayList<User> users = UserManager.getInstance().getUserById(name);

                if (users != null)
                    return new AppResponse(users);
                else
                    throw new HttpBadRequestException(0, "Problem with getting users");
            } catch (Exception e) {
                throw handleException("GET /users/{username}", e);
            }
        }
    }


 @PATCH
 @Consumes({ MediaType.APPLICATION_JSON})
 @Produces({ MediaType.APPLICATION_JSON})
 public AppResponse patchUsers(@Context HttpHeaders headers,
                               Object request,
                               @Nullable @QueryParam("userId") String userId,
                               @Nullable @QueryParam("username") String username){
    JSONObject json = null;
    try{
        json = new JSONObject(ow.writeValueAsString(request));
        User user;
        boolean login = false;
        if(username!=null && !username.equals("")){
            // update
            user = new Gson().fromJson(json.toString(),User.class);
            user.setUsername(username);
        }else{
            // login
            user = new User(userId);
            login = true;
        }

    UserManager.getInstance().updateUser(headers, user, login);

 }catch (Exception e){
 throw handleException("PATCH users/{username}", e);
 }

 return new AppResponse("Update Successful");
 }

 @DELETE

 @Consumes({ MediaType.APPLICATION_JSON })
 @Produces({ MediaType.APPLICATION_JSON })
 public AppResponse deleteUsers(@QueryParam("name") String username){

    try{
        UserManager.getInstance().deleteUser( username);
        return new AppResponse("Delete Successful");
        }catch (Exception e){

        throw handleException("DELETE users/{username}", e);
 }

 }


 }