package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.http.utils.PATCH;
import com.cmu.ratatouille.managers.ConversationManager;
import com.cmu.ratatouille.managers.RecipeManager;
import com.cmu.ratatouille.models.Conversation;
import com.cmu.ratatouille.models.Message;
import com.cmu.ratatouille.models.Recipe;
import com.cmu.ratatouille.utils.AppLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Array;
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
    public AppResponse createConversation(Object request,
                                          @QueryParam("userName") String userName) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Message message = new Gson().fromJson(json.toString(), Message.class);
            message.setTimestamp(System.currentTimeMillis()+"");

            ConversationManager.getInstance().createConversation(message, userName);
            return new AppResponse("Insert Successful");
        } catch (Exception e) {
            throw handleException("POST recipes", e);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getConversation(@Context HttpHeaders headers,
                                       @QueryParam("userName") String userName){
        try{
            AppLogger.info("Got an API call");

            //ArrayList<Recipe> recipes = RecipeManager.getInstance().getRecipeById(recipeId);
            ArrayList<Conversation> conversations = ConversationManager.getInstance().getConversationsByUserName(userName);
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
    public AppResponse patchConversation(Object request,
                                         @QueryParam("conversationId") String conversationId,
                                         @QueryParam("userName") String userName){
        try{
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Message message = new Gson().fromJson(json.toString(), Message.class);
            message.setTimestamp(System.currentTimeMillis()+"");

            ConversationManager.getInstance().updateConversation(message, userName, conversationId);
        }catch (Exception e){
            throw handleException("PATCH conversations", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{recipeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteConversation(@PathParam("recipeId") String recipeId){
        try{
            RecipeManager.getInstance().deleteRecipe(recipeId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE recipes/{recipeId}", e);
        }
    }

//    @GET
//    @Consumes({ MediaType.APPLICATION_JSON })
//    @Produces({ MediaType.APPLICATION_JSON })
//    public AppResponse getConversations(@Context HttpHeaders headers,
//                                  @QueryParam("recipeId") String recipeId,
//                                  @QueryParam("ingredient") String ingredients,
//                                  @QueryParam("calorie") String calorie,
//                                  @QueryParam("rating") String rating,
//                                  @QueryParam("sortby") String sortBy,
//                                  @QueryParam("orderby") String orderBy,
//                                  @QueryParam("offset") Integer offset,
//                                  @QueryParam("count") Integer count){
//        try{
//            ArrayList<Recipe> recipes = new ArrayList<>();
//            if(recipeId!=null){
//                recipes = RecipeManager.getInstance().getRecipeById(recipeId);
//            }else if(ingredients!=null || calorie!=null || rating!=null){
//                // Process raw calorie string
//                String calFrom = "", calTo = "";
//                if(calorie!=null && calorie.contains("to")){
//                    String[] strary = calorie.split("to");
//                    calFrom = strary[0];
//                    calTo = strary[1];
//                }else if(calorie!=null && !calorie.contains("to")){
//                    calFrom = calorie;
//                    calTo = Integer.MAX_VALUE+"";
//                }
//                recipes = RecipeManager.getInstance().getRecipeWithFiltersAndSortings(ingredients, calFrom, calTo, rating, sortBy, orderBy);
//            }else if(offset!=null && count!=null){
//                recipes = RecipeManager.getInstance().getRecipeListPaginated(offset, count);
//            }else{
//                // Get all
//                recipes = RecipeManager.getInstance().getAllRecipes();
//            }
//
//            if(recipes != null)
//                return new AppResponse(recipes);
//            else
//                throw new HttpBadRequestException(0, "Problem with getting recipes");
//        }catch (Exception e){
//            throw handleException("GET recipes", e);
//        }
//    }


}
