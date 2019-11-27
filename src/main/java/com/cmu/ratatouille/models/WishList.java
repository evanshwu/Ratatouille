package com.cmu.ratatouille.models;

import java.util.ArrayList;

public class WishList {
    ArrayList<Recipe> recipe;

    public WishList(ArrayList<Recipe> recipe) {
        this.recipe = recipe;
    }

    public ArrayList<Recipe> getRecipe() {
        return recipe;
    }

    public void setRecipe(ArrayList<Recipe> recipe) {
        this.recipe = recipe;
    }
}
