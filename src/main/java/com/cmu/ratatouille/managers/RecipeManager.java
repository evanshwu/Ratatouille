package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.exceptions.AppUnauthorizedException;
import com.cmu.ratatouille.models.*;
import com.cmu.ratatouille.utils.AppLogger;
import com.cmu.ratatouille.utils.MongoPool;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.operation.OrderBy;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.ws.rs.core.HttpHeaders;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeManager extends Manager {
    public static RecipeManager _self;
    private MongoCollection<Document> recipeCollection;

    public RecipeManager() {
        this.recipeCollection = MongoPool.getInstance().getCollection("Recipes");
    }

    public static RecipeManager getInstance(){
        if (_self == null)
            _self = new RecipeManager();
        return _self;
    }

    public void createRecipe(Recipe recipe) throws AppException {
        try{
//            StringBuilder ingredients = new StringBuilder();
//            for(String ingredient : recipe.getIngredients()) {
//                ingredients.append(ingredient);
//                ingredients.append(",");
//            }
//
//            Document newRecipe = new Document()
//                    .append("recipeId", recipe.getRecipeId())
//                    .append("calorie", recipe.getCalorie())
//                    .append("rating", 0)
//                    .append("image", recipe.getImage())
//                    .append("ingredient", ingredients.toString());
            int count = CounterManager.getInstance().getCounter().getRecipeId();
            Document newRecipe = Document.parse(new Gson().toJson(recipe));
            if (newRecipe != null){
                recipeCollection.insertOne(newRecipe);
                AppLogger.info("Insert new recipe _id="+newRecipe.get("_id").toString());
            }else
                throw new AppInternalServerException(0, "Failed to create new recipe");
        }catch(Exception e){
            throw handleException("Create Recipe", e);
        }
    }

    public void updateRecipe(Recipe recipe) throws AppException {
        try {
            Bson filter = new Document("recipeId", recipe.getRecipeId());
//            StringBuilder ingredients = new StringBuilder();
//            for(String ingredient : recipe.getIngredients()) {
//                ingredients.append(ingredient);
//                ingredients.append(",");
//            }

            Document newRecipe = Document.parse(new Gson().toJson(recipe));
//            Document newRecipe = new Document()
//                    .append("recipeId", recipe.getRecipeId())
//                    .append("recipeName", recipe.getRecipeName())
//                    .append("calorie", recipe.getCalorie())
//                    .append("rating", recipe.getRating())
//                    .append("image", recipe.getImage())
//                    .append("ingredient", ingredients.toString());
            Bson updateOperationDocument = new Document("$set", newRecipe);

            if (newRecipe != null)
                recipeCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update recipe");
        } catch(Exception e) {
            throw handleException("Update Recipe", e);
        }
    }

    public void deleteRecipe(String recipeId) throws AppException {
        try {
            Bson filter = new Document("recipeId", recipeId);
            recipeCollection.deleteOne(filter);
        }catch (Exception e){
            throw handleException("Delete recipe", e);
        }
    }

    public ArrayList<Recipe> getAllRecipes() throws AppException {
        try{
            ArrayList<Recipe> recipes = new ArrayList<>();
            FindIterable<Document> recipeDocs = recipeCollection.find();
            for(Document recipeDoc: recipeDocs) {
                // Get ingredients
//                System.out.println("[EVANSHWU]"+recipeDoc.get("ingredient").toString());
//                List<String> ingredients = new ArrayList<>();
//                for(String str : recipeDoc.getString("ingredient").split(",")){
//                    ingredients.add(str);
//                }
//                // Create recipe object
//                Recipe recipe = new Recipe(
//                        recipeDoc.getString("recipeId"),
//                        recipeDoc.getString("recipeName"),
//                        recipeDoc.getDouble("calorie"),
//                        recipeDoc.getString("image"),
//                        ingredients
//                );
                Recipe recipe = new Gson().fromJson(recipeDoc.toJson(), Recipe.class);
                // Add to list
                recipes.add(recipe);
            }

            return new ArrayList<>(recipes);
        } catch(Exception e){
            throw handleException("Get Recipe List", e);
        }
    }

    public ArrayList<Recipe> getRecipeById(String id) throws AppException {
        try{
            ArrayList<Recipe> recipeList = new ArrayList<>();
            FindIterable<Document> recipeDocs = recipeCollection.find();
            for(Document recipeDoc: recipeDocs) {
                if(recipeDoc.getString("recipeId").equals(id)) {
                    // Get ingredients
//                    System.out.println("[EVANSHWU]"+recipeDoc.get("ingredient").toString());
//                    List<String> ingredients = new ArrayList<>();
//                    for(String str : recipeDoc.getString("ingredient").split(",")){
//                        ingredients.add(str);
//                    }
//                    // Create recipe object
//                    Recipe recipe = new Recipe(
//                            recipeDoc.getString("recipeId"),
//                            recipeDoc.getString("recipeName"),
//                            recipeDoc.getDouble("calorie"),
//                            recipeDoc.getString("image"),
//                            ingredients
//                    );
                    Recipe recipe = new Gson().fromJson(recipeDoc.toJson(), Recipe.class);
                    recipeList.add(recipe);
                }
            }
            return new ArrayList<>(recipeList);
        } catch(Exception e){
            throw handleException("Get Recipe List", e);
        }
    }


    public ArrayList<Recipe> getRecipeWithFiltersAndSortings(String ingredients, String calFrom, String calTo, String ratingFrom, String sortBy, String orderBy) throws AppException {
        AppLogger.info("[ingredients]="+ingredients+"[cal]"+calFrom+calTo+"[rating]"+ratingFrom+"[sortby]"+sortBy+"[orderby]"+orderBy);
        BasicDBObject queryObject = new BasicDBObject();
        List<Bson> filters = new ArrayList<>();
        List<BasicDBObject> queryObjectList = new ArrayList<>();
        // Check ingredients
        if(ingredients!=null && !ingredients.equals("")){
            AppLogger.info("[getRecipeWithFiltersAndSortings] ingredient query: "+ingredients);
            Bson ingredientFilter = Filters.regex("ingredient", ingredients);
            filters.add(ingredientFilter);
            BasicDBObject containsQuery = new BasicDBObject();
            containsQuery.put("ingredient", "/"+ingredients+"/");
            queryObjectList.add(containsQuery);
        }
        //Check calorie range
        if(!calFrom.equals("") && !calTo.equals("")){
            AppLogger.info("[getRecipeWithFiltersAndSortings] calorie query: "+calFrom.split("\\.")[0]+"~"+calTo.split("\\.")[0]);
            Bson calorieRangeFilter = Filters.and(
                    Filters.gte("calorie", Integer.parseInt(calFrom.split("\\.")[0])),
                    Filters.lte("calorie", Integer.parseInt(calTo.split("\\.")[0])));
            filters.add(calorieRangeFilter);
            BasicDBObject gtCalQuery = new BasicDBObject();
            gtCalQuery.put("calorie", new BasicDBObject("$gt", Integer.parseInt(calFrom.split("\\.")[0])).append("$lt", Integer.parseInt(calTo.split("\\.")[0])));
            queryObjectList.add(gtCalQuery);
        }
        // Check rating range
        if(ratingFrom!=null && !ratingFrom.equals("")) {
            AppLogger.info("[getRecipeWithFiltersAndSortings] ratings query: "+ratingFrom+"~5");
            Bson ratingFilter = Filters.gte("rating", ratingFrom);
            filters.add(ratingFilter);
            BasicDBObject gtRateQuery = new BasicDBObject();
            gtRateQuery.put("rating", new BasicDBObject("$gt", Double.parseDouble(ratingFrom)).append("$lt", 5));
            queryObjectList.add(gtRateQuery);
        }

        System.out.println("Filter="+filters.toString());
        FindIterable<Document> recipeDocs;
        if(filters.size()>1)
            recipeDocs = recipeCollection.find(Filters.and(filters));
        else
            recipeDocs = recipeCollection.find(filters.get(0));
        // Check if sorting is requested

        if(orderBy!=null && sortBy!=null && !orderBy.equals("") && !sortBy.equals("")) {
            AppLogger.info("[getRecipeWithFiltersAndSortings] Got sorting: sortBy="+sortBy+", orderBy="+orderBy);
            if(orderBy.toLowerCase().equals("asc"))
                recipeDocs = recipeDocs.sort(Sorts.ascending(sortBy));
            else
                recipeDocs = recipeDocs.sort(Sorts.descending(sortBy));
        }

        ArrayList<Recipe> recipeList = new ArrayList<>();
        for(Document recipeDoc: recipeDocs) {
            // Get ingredients
//            System.out.println("[EVANSHWU]"+recipeDoc.get("ingredient").toString());
//            List<String> _ingredients = new ArrayList<>();
//            for(String str : recipeDoc.getString("ingredient").split(",")){
//                _ingredients.add(str);
//            }
//            // Create recipe object
//            Recipe recipe = new Recipe(
//                    recipeDoc.getString("recipeId"),
//                    recipeDoc.getString("recipeName"),
//                    recipeDoc.getDouble("calorie"),
//                    recipeDoc.getString("image"),
//                    _ingredients
//            );
            Recipe recipe = new Gson().fromJson(recipeDoc.toJson(), Recipe.class);
            System.out.println("Got recipe "+recipe.getRecipeName());
            recipe.setRating(recipeDoc.getDouble("rating"));
            // Add to list
            recipeList.add(recipe);
        }

        return recipeList;
    }

    public ArrayList<Recipe> getRecipeListPaginated(Integer offset, Integer count) throws AppException {
        try{
            ArrayList<Recipe> recipeList = new ArrayList<>();
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put("recipeId", OrderBy.ASC.getIntRepresentation());
            FindIterable<Document> recipeDocs = recipeCollection.find().sort(sortParams).skip(offset).limit(count);
            for(Document recipeDoc: recipeDocs) {
//                // Get ingredients
//                List<String> _ingredients = new ArrayList<>();
//                for(String str : recipeDoc.getString("ingredient").split(",")){
//                    _ingredients.add(str);
//                }
//                // Create recipe object
//                Recipe recipe = new Recipe(
//                        recipeDoc.getString("recipeId"),
//                        recipeDoc.getString("recipeName"),
//                        recipeDoc.getDouble("calorie"),
//                        recipeDoc.getString("image"),
//                        _ingredients
//                );
                Recipe recipe = new Gson().fromJson(recipeDoc.toJson(), Recipe.class);
                recipeList.add(recipe);
            }
            return new ArrayList<>(recipeList);
        } catch(Exception e){
            throw handleException("Get Recipe List", e);
        }
    }

    public void submitRating(HttpHeaders headers, String recipeId, double rating, String userId) throws AppException{
        try{
            Session session = SessionManager.getInstance().getSessionForToken(headers);
            if(!session.getUserId().equals(userId)){
                throw new AppUnauthorizedException(70,"Invalid user id");
            }

            ArrayList<Recipe> recipes = this.getRecipeById(recipeId);
            System.out.println("Got recipe" + recipes.get(0).getRecipeId());
            if(recipes.size()>0){
                Recipe recipe = recipes.get(0);
                double newRate = (recipe.getRating() * recipe.rater + rating) / (recipe.rater+1);
                recipe.setRating(newRate);
                recipe.rater += 1;
                this.updateRecipe(recipe);
                return;
            }
            throw new AppInternalServerException(0, "Failed to submit rate");
        }catch(Exception e){
            throw handleException("Submit rating", e);
        }
    }

    public void insertRecipe(String query, int from, int to) throws AppException{
        try{
            // Fetch JSON from api url
            URL url = new URL("https://api.edamam.com/search?app_id=3ef87764&app_key=f6329aeb0ce6a806b529977877a9b5a4%20&q="+query+"&from="+from+"&to="+to);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String json = "";
            String str;
            while (null != (str = br.readLine())) {
                json+=str;
            }
            System.out.println(url.toString());
            System.out.println(json);

            // Build object and insert into database
            RecipeQueryRes queryRes = new Gson().fromJson(json, RecipeQueryRes.class);
            for(Hit hit: queryRes.getHits()){
                RecipeFromApi recipeFromApi = hit.getRecipe();
                Recipe recipe = new Recipe("R"+CounterManager.getInstance().pushCount(),
                        recipeFromApi.getLabel(),
                        recipeFromApi.getCaloriesAsDouble(),
                        recipeFromApi.getImage(),
                        recipeFromApi.getIngredients());
                recipe.setRating(5);
                createRecipe(recipe);
            }
        }catch (Exception ex){
            throw handleException("Error inserting recipes to DB!", ex);
        }
    }

}
