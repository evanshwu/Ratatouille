package com.cmu.ratatouille.models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    String recipeId;
    String recipeName;
    double calorie;
    String image;
    List<Ingredient> ingredients;
    double rating;
    public int rater;

    /**
     * Constructor for creating recipe obj.
     * @param recipeId UID for recipe, this is not the same id as mongoDB's _id
     * @param recipeName Name of recipe
     * @param calorie Calorie in double value
     * @param image Url to image
     * @param ingredients List of ingredients
     */
    public Recipe(String recipeId, String recipeName, double calorie, String image, List<Ingredient> ingredients) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.calorie = calorie;
        this.image = image;
        this.ingredients = ingredients;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
}
