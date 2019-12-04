package com.cmu.ratatouille.models;

public class Hit {
    public RecipeFromApi recipe;
    public String bookmarked;
    public String bought;

    public RecipeFromApi getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeFromApi recipe) {
        this.recipe = recipe;
    }

    public String getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(String bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String getBought() {
        return bought;
    }

    public void setBought(String bought) {
        this.bought = bought;
    }
}
