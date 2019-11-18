package com.cmu.ratatouille.models;

import java.util.ArrayList;

public class FavoriteList {
    double score;
    ArrayList<Recipe> recipe;
    int favoriteId;

    public FavoriteList(double score, ArrayList<Recipe> recipe, int favoriteId) {
        this.score = score;
        this.recipe = recipe;
        this.favoriteId = favoriteId;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ArrayList<Recipe> getRecipe() {
        return recipe;
    }

    public void setRecipe(ArrayList<Recipe> recipe) {
        this.recipe = recipe;
    }
}
