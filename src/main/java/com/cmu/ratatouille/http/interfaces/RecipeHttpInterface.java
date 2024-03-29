package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.http.utils.PATCH;
import com.cmu.ratatouille.managers.RecipeManager;
import com.cmu.ratatouille.models.Recipe;
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

@Path("/recipes")

public class RecipeHttpInterface extends HttpInterface {
    private ObjectWriter ow;

    public RecipeHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postRecipes(Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Recipe newRecipe = new Gson().fromJson(json.toString(), Recipe.class);
            RecipeManager.getInstance().createRecipe(newRecipe);
            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST recipes", e);
        }
    }

    @GET
    @Path("/{recipeId}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getRecipe(@Context HttpHeaders headers, @PathParam("recipeId") String recipeId){
        try{
            AppLogger.info("Got an API call");
            ArrayList<Recipe> recipes = RecipeManager.getInstance().getRecipeById(recipeId);
            if(recipes != null)
                return new AppResponse(recipes);
            else
                throw new HttpBadRequestException(0, "Problem with getting recipes");
        }catch (Exception e){
            throw handleException("GET /recipes/{recipeId}", e);
        }
    }

    @PATCH
    @Path("/{recipeId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchRecipes(Object request, @PathParam("recipeId") String recipeId){
        try{
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Recipe recipe = new Gson().fromJson(json.toString(), Recipe.class);
            RecipeManager.getInstance().updateRecipe(recipe);
        }catch (Exception e){
            throw handleException("PATCH recipes/{recipeId}", e);
        }
        return new AppResponse("Update Successful");
    }

    @DELETE
    @Path("/{recipeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteRecipes(@PathParam("recipeId") String recipeId){
        try{
            RecipeManager.getInstance().deleteRecipe(recipeId);
            return new AppResponse("Delete Successful");
        }catch (Exception e){
            throw handleException("DELETE recipes/{recipeId}", e);
        }
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse getRecipes(@Context HttpHeaders headers,
                                  @QueryParam("recipeId") String recipeId,
                                  @QueryParam("ingredient") String ingredients,
                                  @QueryParam("calorie") String calorie,
                                  @QueryParam("rating") String rating,
                                  @QueryParam("sortby") String sortBy,
                                  @QueryParam("orderby") String orderBy,
                                  @QueryParam("offset") Integer offset,
                                  @QueryParam("count") Integer count){
        try{
            ArrayList<Recipe> recipes = new ArrayList<>();
            if(recipeId!=null){
                recipes = RecipeManager.getInstance().getRecipeById(recipeId);
            }else if(ingredients!=null || calorie!=null || rating!=null){
                // Process raw calorie string
                String calFrom = "", calTo = "";
                if(calorie!=null && calorie.contains("to")){
                    String[] strary = calorie.split("to");
                    calFrom = strary[0];
                    calTo = strary[1];
                }else if(calorie!=null && !calorie.contains("to")){
                    calFrom = calorie;
                    calTo = Integer.MAX_VALUE+"";
                }
                recipes = RecipeManager.getInstance().getRecipeWithFiltersAndSortings(ingredients, calFrom, calTo, rating, sortBy, orderBy);
            }else if(offset!=null && count!=null){
                recipes = RecipeManager.getInstance().getRecipeListPaginated(offset, count);
            }else{
                // Get all
                recipes = RecipeManager.getInstance().getAllRecipes();
            }

            if(recipes != null)
                return new AppResponse(recipes);
            else
                throw new HttpBadRequestException(0, "Problem with getting recipes");
        }catch (Exception e){
            throw handleException("GET recipes", e);
        }
    }

    @POST
    @Path("/rate/{recipeId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postRatings(@Context HttpHeaders headers,
                                   Object request,
                                   @PathParam("recipeId") String recipeId,
                                   @QueryParam("userId") String userId) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Double rating = json.getDouble("rating");

            RecipeManager.getInstance().submitRating(headers, recipeId, rating, userId);
            return new AppResponse("Insert Successful");

        } catch (Exception e) {
            throw handleException("POST recipes", e);
        }
    }

    @GET
    @Path("/insert")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse insertRecipe(@Context HttpHeaders headers,
                                    @QueryParam("query") String query,
                                    @QueryParam("from") int indexFrom,
                                    @QueryParam("to") int indexTo){
        try{
            RecipeManager.getInstance().insertRecipe(query, indexFrom, indexTo);
            return new AppResponse("Insert successful");
        }catch (Exception e){
            throw handleException("GET /insert", e);
        }
    }


}
