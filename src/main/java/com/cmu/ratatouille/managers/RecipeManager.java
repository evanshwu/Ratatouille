package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.exceptions.AppInternalServerException;
import com.cmu.ratatouille.models.Book;
import com.cmu.ratatouille.models.Recipe;
import com.cmu.ratatouille.utils.MongoPool;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.operation.OrderBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager extends Manager {

//    private static final Logger logger = LogManager.getLogger(getInstance().getClass().getName());
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
//            JSONObject json = new JSONObject(recipe);
            StringBuilder ingredients = new StringBuilder();
            for(String ingredient : recipe.getIngredients()) {
                ingredients.append(ingredient);
                ingredients.append(",");
            }

            Document newRecipe = new Document()
                    // TODO: Create UID generator
                    .append("recipeId", recipe.getRecipeId())
                    .append("calorie", recipe.getCalorie())
                    .append("rating", recipe.getRating())
                    .append("image", recipe.getImage())
                    .append("ingredient", ingredients.toString());
            if (newRecipe != null){
                recipeCollection.insertOne(newRecipe);
//                logger.info("Insert new recipe _id="+newRecipe.get("_id").toString());
            }else
                throw new AppInternalServerException(0, "Failed to create new recipe");
        }catch(Exception e){
            throw handleException("Create Recipe", e);
        }
    }

    public void updateRecipe(Recipe recipe) throws AppException {
        try {
            Bson filter = new Document("book_id", recipe.getRecipeId());
            StringBuilder ingredients = new StringBuilder();
            for(String ingredient : recipe.getIngredients()) {
                ingredients.append(ingredient);
                ingredients.append(",");
            }

            Document newRecipe = new Document()
                    .append("recipeId", recipe.getRecipeId())
                    .append("calorie", recipe.getCalorie())
                    .append("rating", recipe.getRating())
                    .append("image", recipe.getImage())
                    .append("ingredient", ingredients.toString());
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
                System.out.println("[EVANSHWU]"+recipeDoc.get("ingredient").toString());
                List<String> ingredients = new ArrayList<>();
                for(String str : recipeDoc.getString("ingredient").split(",")){
                    ingredients.add(str);
                }
                // Create recipe object
                Recipe recipe = new Recipe(
                        recipeDoc.getString("recipeId"),
                        recipeDoc.getDouble("calorie"),
                        recipeDoc.getString("image"),
                        ingredients,
                        recipeDoc.getDouble("rating")
                );
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
                    System.out.println("[EVANSHWU]"+recipeDoc.get("ingredient").toString());
                    List<String> ingredients = new ArrayList<>();
                    for(String str : recipeDoc.getString("ingredient").split(",")){
                        ingredients.add(str);
                    }
                    // Create recipe object
                    Recipe recipe = new Recipe(
                            recipeDoc.getString("recipeId"),
                            recipeDoc.getDouble("calorie"),
                            recipeDoc.getString("image"),
                            ingredients,
                            recipeDoc.getDouble("rating")
                    );
                    recipeList.add(recipe);
                }
            }
            return new ArrayList<>(recipeList);
        } catch(Exception e){
            throw handleException("Get Recipe List", e);
        }
    }

    //TODO: Test required
    public ArrayList<Recipe> getRecipeWithFiltersAndSortings(ArrayList<String> ingredients, String calFrom, String calTo, String ratingFrom, String ratingTo, String sortBy, String orderBy) throws AppException {
        BasicDBObject queryObject = new BasicDBObject();
        List<BasicDBObject> queryObjectList = new ArrayList<>();
        // Check ingredients
        if(ingredients!=null && ingredients.size()>0){
            BasicDBObject andQuery = new BasicDBObject();
            andQuery.put("ingredient", new BasicDBObject("&in", ingredients));
            queryObjectList.add(andQuery);
        }
        //Check calorie range
        if(calFrom!=null && calTo!=null){
            BasicDBObject gtCalQuery = new BasicDBObject();
            gtCalQuery.put("calorie", new BasicDBObject("$gt", calFrom).append("$lt", calTo));
            queryObjectList.add(gtCalQuery);
        }
        // Check rating range
        if(ratingFrom!=null && ratingTo!=null) {
            BasicDBObject gtRateQuery = new BasicDBObject();
            gtRateQuery.put("rating", new BasicDBObject("$gt", ratingFrom).append("$lt", ratingTo));
            queryObjectList.add(gtRateQuery);
        }
        // Combine all query options
        queryObject.put("$and", queryObjectList);
        // Check if sorting is requested
         FindIterable<Document> recipeDocs;
        if(!sortBy.equals("") && !orderBy.equals("")){
            if(orderBy.equals("DESC"))
                recipeDocs = recipeCollection.find(queryObject).sort(new BasicDBObject(sortBy, OrderBy.DESC.getIntRepresentation()));
            else
                recipeDocs = recipeCollection.find(queryObject).sort(new BasicDBObject(sortBy, OrderBy.ASC.getIntRepresentation()));
        }else{
            recipeDocs = recipeCollection.find(queryObject);
        }

        ArrayList<Recipe> recipeList = new ArrayList<>();
        for(Document recipeDoc: recipeDocs) {
            // Get ingredients
            System.out.println("[EVANSHWU]"+recipeDoc.get("ingredient").toString());
            List<String> _ingredients = new ArrayList<>();
            for(String str : recipeDoc.getString("ingredient").split(",")){
                _ingredients.add(str);
            }
            // Create recipe object
            Recipe recipe = new Recipe(
                    recipeDoc.getString("recipeId"),
                    recipeDoc.getDouble("calorie"),
                    recipeDoc.getString("image"),
                    _ingredients,
                    recipeDoc.getDouble("rating")
            );
            // Add to list
            recipeList.add(recipe);
        }

        return recipeList;
    }

}
