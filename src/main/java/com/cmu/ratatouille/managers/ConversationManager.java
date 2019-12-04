package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.exceptions.AppUnauthorizedException;
import com.cmu.ratatouille.models.Conversation;
import com.cmu.ratatouille.models.Message;
import com.cmu.ratatouille.models.Session;
import com.cmu.ratatouille.models.User;
import com.cmu.ratatouille.utils.MongoPool;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;

public class ConversationManager extends Manager{
    public static ConversationManager _self;
    private MongoCollection<Document> conversationCollection;

    public ConversationManager(){
        this.conversationCollection = MongoPool.getInstance().getCollection("Conversations");
    }

    public static ConversationManager getInstance(){
        if(_self==null)
            _self = new ConversationManager();
        return _self;
    }

    public void createConversation(HttpHeaders headers, Message message, String userId) throws AppException{
        try{
            Session session = SessionManager.getInstance().getSessionForToken(headers);
            if(!session.getUserId().equals(userId)){
                throw new AppUnauthorizedException(70,"Invalid user id");
            }

            // Get user name
            User user = UserManager.getInstance().getUserById(headers, userId);
            message.setUserName(user.getUsername());

            ArrayList<Message> _messages = new ArrayList<>();
            _messages.add(message);
            Conversation conversation = new Conversation(null, userId, _messages);

            Document document = Document.parse(new Gson().toJson(conversation));

            if(document!=null)
                conversationCollection.insertOne(document);
            else
                throw new AppInternalServerException(0, "Error creating new conversation");
        }catch(Exception ex){
            throw handleException("createConversation", ex);
        }
    }

    public void updateConversation(HttpHeaders headers, Message message, String userId, String conversationId) throws AppException{
        try{
            Session session = SessionManager.getInstance().getSessionForToken(headers);
            if(!session.getUserId().equals(userId)){
                throw new AppUnauthorizedException(70,"Invalid user id");
            }

            // Get user name
            User user = UserManager.getInstance().getUserById(headers, userId);
            message.setUserName(user.getUsername());

            Conversation conversation = getConversationById(conversationId);
            conversation.getMessages().add(message);
            Bson newValue = Document.parse(new Gson().toJson(conversation));

            Bson filter = new Document("_id", new ObjectId(conversationId));
            Bson updateOperationDocument = new Document("$set", newValue);

            if(conversation!=null)
                conversationCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Error updating conversation");
        }catch(Exception ex){
            throw handleException("updateConversation", ex);
        }
    }

    public Conversation getConversationById(String conversationId) throws AppException{
        try{

            Conversation conversation = null;
            FindIterable<Document> conversationDocs = conversationCollection.find();

            for(Document conversationDoc : conversationDocs){
                if(conversationDoc.getObjectId("_id").toString().equals(conversationId)){
                    System.out.println("Got record!");
                    conversation = new Gson().fromJson(conversationDoc.toJson(), Conversation.class);
                    return conversation;
                }
            }
            throw new AppInternalServerException(0, "Error getting conversation based on conversation id");
        }catch (Exception ex){
            throw handleException("getConversationById", ex);
        }
    }

    public ArrayList<Conversation> getConversationsByUserId(HttpHeaders headers, String userId) throws AppException{
        try{
            Session session = SessionManager.getInstance().getSessionForToken(headers);
            if(!session.getUserId().equals(userId)){
                throw new AppUnauthorizedException(70,"Invalid user id");
            }

            ArrayList<Conversation> conversations = new ArrayList<>();
            Bson filter = new Document("userId", userId);

            FindIterable<Document> conversationDocs;
            if(userId == null || userId.equals(""))
                conversationDocs = conversationCollection.find();
            else
                conversationDocs = conversationCollection.find(filter);
            for(Document conversationDoc : conversationDocs){
                System.out.println("raw conversation json is "+conversationDoc.toJson());
                conversations.add(new Gson().fromJson(conversationDoc.toJson(), Conversation.class));
            }
            return conversations;
        }catch (Exception ex){
            throw handleException("getConversationsByUserId", ex);
        }
    }
}
