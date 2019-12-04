package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppBadRequestException;
import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.exceptions.AppUnauthorizedException;
import com.cmu.ratatouille.models.Session;
import com.cmu.ratatouille.models.User;
import com.cmu.ratatouille.utils.MongoPool;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.validator.routines.EmailValidator;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

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
            // Check email
            if(!EmailValidator.getInstance().isValid(user.getEmail())){
                throw new AppBadRequestException(0, "Incorrect email!");
            }

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

    public void updateUser(HttpHeaders headers, User user, boolean admin) throws AppException {
        try {
            if(!admin){
                Session session = SessionManager.getInstance().getSessionForToken(headers);
                if(!session.getUserId().equals(user.getUserId())){
                    throw new AppUnauthorizedException(70,"Invalid user id");
                }
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

    public User getUserById(HttpHeaders headers, String userId) throws AppException{
        try{
            Session session = SessionManager.getInstance().getSessionForToken(headers);
            if(!session.getUserId().equals(userId)){
                throw new AppUnauthorizedException(70,"Invalid user id");
            }
            Bson filter = new Document("_id", new ObjectId(userId));
            Document userDoc = userCollection.find(filter).first();
            if(userDoc!=null){
                User user = new Gson().fromJson(userDoc.toJson(), User.class);
                user.setUserId(userId);
                return user;
            }else{
                throw new AppBadRequestException(0, "User not found");
            }
        }catch(Exception ex){
            throw handleException("Get single user", ex);
        }
    }


}
