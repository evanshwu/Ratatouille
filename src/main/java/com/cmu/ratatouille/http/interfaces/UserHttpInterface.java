package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.http.utils.PATCH;
import com.cmu.ratatouille.managers.UserManager;
import com.cmu.ratatouille.models.User;
import com.cmu.ratatouille.utils.MongoPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Sorts;
import jdk.internal.jline.internal.Nullable;
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
                                   @QueryParam("name") String name,
                                   @QueryParam("userId") String userId) {
        if(userId!=null && !userId.equals("")){
            try{
                User user = UserManager.getInstance().getUserById(headers, userId);
                if(user!=null)
                    return new AppResponse(user);
                else
                    throw new HttpBadRequestException(0, "No user found with such userId");
            }catch(Exception ex){
                throw handleException("GET /users", ex);
            }
        } else if (sortBy == null && pageSize == null && page == null && name == null) {
            try {
                JSONObject json = null;
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
                FindIterable<Document> userDocs = MongoPool.getInstance().getCollection("users").find().sort(Sorts.ascending(sortBy));
                ArrayList<User> userList = new ArrayList<>();
                for (Document userDoc : userDocs) {
                    User _userList = new Gson().fromJson(userDoc.toJson(), User.class);
                    userList.add(_userList);
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
                    User _userList = new Gson().fromJson(userDoc.toJson(), User.class);
                    userList.add(_userList);
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


    @PATCH
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse patchUsers(@Context HttpHeaders headers,
                                  Object request,
                                  @Nullable @QueryParam("userId") String userId,
                                  @Nullable @QueryParam("username") String username) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            User user;
            user = new Gson().fromJson(json.toString(), User.class);
            if (username != null && !username.equals(""))
                user.setUsername(username);
            if (userId != null && !userId.equals(""))
                user.setUserId(userId);

            UserManager.getInstance().updateUser(headers, user, false);

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