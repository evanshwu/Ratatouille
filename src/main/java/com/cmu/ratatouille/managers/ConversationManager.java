package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.models.Conversation;
import com.cmu.ratatouille.utils.MongoPool;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.lang.reflect.Array;
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

    public void createConversation(Conversation conversation) throws AppException{

    }

    public void updateConversation(Conversation conversation) throws AppException{

    }

//    public Conversation getConversationById(String conversationId) throws AppException{
//        try{
//            Conversation conversations;
//            FindIterable<Document> conversationDocs = conversationCollection.find(new Document("id", conversationId));
//            for(Document conversationDoc : conversationDocs){
//                conversations = new Conversation(conversationDoc.getString("id"),
//                        conversationDoc.getString("userId"),
//                        conver);
//            }
//
//
//            return conversations;
//        }catch (Exception ex){
//            throw handleException("getConversationById", ex);
//        }
//    }

    public ArrayList<Conversation> getConversationsByUserId(String userId) throws AppException{
        try{
            ArrayList<Conversation> conversations = new ArrayList<>();


            return conversations;
        }catch (Exception ex){
            throw handleException("getConversationsByUserId", ex);
        }
    }
}
