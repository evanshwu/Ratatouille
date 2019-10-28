package com.cmu.ratatouille.http.interfaces;

import com.cmu.ratatouille.http.exceptions.HttpBadRequestException;
import com.cmu.ratatouille.http.responses.AppResponse;
import com.cmu.ratatouille.http.utils.PATCH;
import com.cmu.ratatouille.managers.RecipeManager;
import com.cmu.ratatouille.models.Recipe;
import com.cmu.ratatouille.utils.AppLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.json.JSONArray;
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

            ArrayList<String> ingredients = new ArrayList<>();
            JSONArray ingredientArray = json.getJSONArray("ingredients");
            System.out.println("[SIZE]"+ingredientArray.length());
            for(int i=0;i<ingredientArray.length();i++)
                ingredients.add(ingredientArray.getString(i));

            Recipe newRecipe = new Recipe(
                    json.getString("recipeId"),
                    json.getString("recipeName"),
                    json.getDouble("calorie"),
                    json.getString("image"),
                    ingredients,
                    json.getDouble("rating")
            );
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

            ArrayList<String> ingredients = new ArrayList<>();
            JSONArray ingredientArray = json.getJSONArray("ingredients");
            for(int i=0;i<ingredientArray.length();i++)
                ingredients.add(ingredientArray.getString(i));

            Recipe recipe = new Recipe(
                    json.getString("recipeId"),
                    json.getString("recipeName"),
                    json.getDouble("calorie"),
                    json.getString("image"),
                    ingredients,
                    json.getDouble("rating")
            );
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


}
