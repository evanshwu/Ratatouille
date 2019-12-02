package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.http.utils.PATCH;
import com.cmu.ratatouille.managers.ConversationManager;
import com.cmu.ratatouille.managers.RecipeManager;
import com.cmu.ratatouille.models.Conversation;
import com.cmu.ratatouille.models.Message;
import com.cmu.ratatouille.utils.AppLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/conversations")

public class ConversationHttpInterface extends HttpInterface {
    private ObjectWriter ow;

    public ConversationHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse createConversation(@Context HttpHeaders headers,
                                          Object request,
                                          @QueryParam("userId") String userId) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Message message = new Gson().fromJson(json.toString(), Message.class);
            message.setTimestamp(System.currentTimeMillis()+"");

            ConversationManager.getInstance().createConversation(headers, message, userId);
            return new AppResponse("Insert Successful");
        } catch (Exception e) {
            throw handleException("POST recipes", e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getConversation(@Context HttpHeaders headers,
                                       @QueryParam("userId") String userId){
        try{
            ArrayList<Conversation> conversations = ConversationManager.getInstance().getConversationsByUserId(headers, userId);
            if(conversations.size()!=0)
                return new AppResponse(conversations);
            else
                throw new HttpBadRequestException(0, "Problem with getting conversations");
        }catch (Exception e){
            throw handleException("GET /conversations", e);
        }
    }

    @PATCH
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchConversation(@Context HttpHeaders headers,
                                         Object request,
                                         @QueryParam("conversationId") String conversationId,
                                         @QueryParam("userId") String userId){
        try{
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Message message = new Gson().fromJson(json.toString(), Message.class);
            message.setTimestamp(System.currentTimeMillis()+"");

            ConversationManager.getInstance().updateConversation(headers, message, userId, conversationId);
        }catch (Exception e){
            throw handleException("PATCH conversations", e);
        }
        return new AppResponse("Update Successful");
    }

}
