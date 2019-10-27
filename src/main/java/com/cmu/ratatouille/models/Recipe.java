package com.cmu.ratatouille.models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    String recipeId;
    double calorie;
    String image;
    List<String> ingredients;
    double rating;

    /**
     * Constructor for creating recipe obj.
     * @param recipeId
     * @param calorie
     * @param image
     * @param ingredients
     * @param rating
     */
    public Recipe(String recipeId, double calorie, String image, List<String> ingredients, double rating) {
        this.recipeId = recipeId;
        this.calorie = calorie;
        this.image = image;
        this.ingredients = ingredients;
        this.rating = rating;
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

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
