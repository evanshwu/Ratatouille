package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.http.utils.PATCH;
import com.cmu.ratatouille.managers.UserManager;
import com.cmu.ratatouille.models.User;
import com.cmu.ratatouille.utils.AppLogger;
import com.cmu.ratatouille.utils.MongoPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/users")

public class UserHttpInterface extends HttpInterface {
    private ObjectWriter ow;

    public UserHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postUsers(Object request) {
        try {
            JSONObject json = new JSONObject(ow.writeValueAsString(request));
            User newuser = new Gson().fromJson(json.toString(), User.class);
            UserManager.getInstance().createUser(newuser);
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
        if (sortBy == null && pageSize == null && page == null && name == null) {
            try {
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
                FindIterable<Document> userDocs = MongoPool.getInstance().getCollection("users").find().sort(Sorts.ascending(sortBy));
                ArrayList<User> userList = new ArrayList<>();
                for (Document userDoc : userDocs) {
                    User user = new Gson().fromJson(userDoc.toJson(), User.class);
                    user.setUserId(userDoc.getObjectId("_id").toString());
//                    User user = new User(
//                            userDoc.getObjectId("_id").toString(),
//                            userDoc.getString("username"),
//                            userDoc.getString("email"),
//                            userDoc.getDouble("height"),
//                            userDoc.getDouble("weight"),
//                            userDoc.getInteger("age"),
//                            userDoc.getString("gender")
//                    );
                    userList.add(user);
                }
                if (userList != null)
                    return new AppResponse(userList);
                else
                    throw new HttpBadRequestException(0, "Problem with getting users");

            } catch (Exception e) {
                throw handleException("GET /users?sortby={username}", e);
            }
        } else if (sortBy == null && page != null && pageSize != null) {
            try {
                FindIterable<Document> userDocs = MongoPool.getInstance().getCollection("users").find().skip(pageSize * (page - 1)).limit(pageSize);
                ArrayList<User> userList = new ArrayList<>();
                for (Document userDoc : userDocs) {
                    User user = new Gson().fromJson(userDoc.toJson(), User.class);
                    user.setUserId(userDoc.getObjectId("_id").toString());
//                    User user = new User(
//                            userDoc.getObjectId("_id").toString(),
//                            userDoc.getString("username"),
//                            userDoc.getString("email"),
//                            userDoc.getDouble("height"),
//                            userDoc.getDouble("weight"),
//                            userDoc.getInteger("age"),
//                            userDoc.getString("gender")
//                    );
                    userList.add(user);
                }
                if (userList != null)
                    return new AppResponse(userList);
                else
                    throw new HttpBadRequestException(0, "Problem with getting users");
            } catch (Exception e) {
                throw handleException("GET /users?sortby={username}", e);
            }
        } else {
            try {
                AppLogger.info("Got an API call");
                ArrayList<User> users = UserManager.getInstance().getUserByName(name);

                if (users != null)
                    return new AppResponse(users);
                else
                    throw new HttpBadRequestException(0, "Problem with getting users");
            } catch (Exception e) {
                throw handleException("GET /users/{username}", e);
            }
        }
    }

    //TODO: Patch user does not change wishlist, favoritelist, and meal history
    @PATCH
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse patchUsers(Object request, @QueryParam("name") String name) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            User user = new User(
                    null,
                    json.getString("username"),
                    json.getString("email"),
                    json.getDouble("height"),
                    json.getDouble("weight"),
                    json.getInt("age"),
                    json.getString("gender")
            );
            UserManager.getInstance().updateUser(user);
        } catch (Exception e) {
            throw handleException("PATCH users/{username}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse deleteUsers(@QueryParam("name") String username) {
        try {
            UserManager.getInstance().deleteUser(username);
            return new AppResponse("Delete Successful");
        } catch (Exception e) {
            throw handleException("DELETE users/{username}", e);
        }

    }



}