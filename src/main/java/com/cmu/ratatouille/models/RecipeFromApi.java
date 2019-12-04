package com.cmu.ratatouille.models;

import java.util.ArrayList;

public class RecipeFromApi {
    public String uri;
    public String label;
    public String image;
    public String source;
    public String url;
    public String yield;
    public String calories;
    public ArrayList<Ingredient> ingredients;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getYield() {
        return yield;
    }

    public void setYield(String yield) {
        this.yield = yield;
    }

    public String getCalories() {
        return calories;
    }

    public double getCaloriesAsDouble(){
        String[] cal = this.calories.split("\\.");
        return Double.parseDouble(cal[0]);
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
