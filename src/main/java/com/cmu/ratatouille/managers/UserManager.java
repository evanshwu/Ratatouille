package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppUnauthorizedException;
import com.cmu.ratatouille.http.utils.StringUtil;
import com.cmu.ratatouille.models.FavoriteList;
import com.cmu.ratatouille.models.Session;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.models.User;
import com.cmu.ratatouille.utils.MongoPool;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;

public class UserManager extends Manager {
    public static UserManager _self;
    private MongoCollection<Document> userCollection;


    public UserManager() {
        this.userCollection = MongoPool.getInstance().getCollection("users");
    }

    public static UserManager getInstance(){
        if (_self == null)
            _self = new UserManager();
        return _self;
    }

    public void createUser(User user) throws AppException {
        try{
            Gson gson = new Gson();
            Document newDoc = Document.parse(gson.toJson(user));
            if (newDoc != null){
                userCollection.insertOne(newDoc);
                MealManager.getInstance().pushMeal(user);
            }else
                throw new AppInternalServerException(0, "Failed to create new user");

        }catch(Exception e){
            throw handleException("Create User", e);
        }

    }

    public void updateUser(HttpHeaders headers, User user, boolean login) throws AppException {
        try {
            if(login){
                Session session = SessionManager.getInstance().getSessionForToken(headers);
                System.out.println("Login activity");
                if(!session.getUserId().equals(user.getUserId())){
                    throw new AppUnauthorizedException(70,"Invalid user id");
                }else
                    return;
            }

            Bson filter = new Document("username", user.getUsername());
            Gson gson =new Gson();
            Bson newValue = Document.parse(gson.toJson(user));
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                userCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update user details");

        } catch(Exception e) {
            throw handleException("Update User", e);
        }
    }

    public void deleteUser(String username) throws AppException {
        try {
            Bson filter = new Document("username", username);
            userCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete User", e);
        }
    }

    public ArrayList<User> getUserList() throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            FindIterable<Document> userDocs = userCollection.find();

            for(Document userDoc: userDocs) {
                User _userList = new Gson().fromJson(userDoc.toJson(),User.class);
                _userList.setUserId(userDoc.getObjectId("_id")+"");
                userList.add(_userList);
            }

            return userList;
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getUserByName(String username) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            FindIterable<Document> userDocs = userCollection.find();
            for(Document userDoc: userDocs) {
                if(userDoc.getString("username").equals(username)) {
                    User _userList = new Gson().fromJson(userDoc.toJson(),User.class);
                    userList.add(_userList);
                }
            }
            return new ArrayList<>(userList);
        } catch(Exception e){
            throw handleException("Get User List", e);
        }
    }


}
