package com.cmu.ratatouille.models;


import java.util.ArrayList;

public class User {
    String userId;
    String username;
    String email;
    char[] password;
    WishList wishList;
    FavoriteList favoriteList;
    double height;
    double weight;
    int age;
    String gender;
    ArrayList<String> type;
    ArrayList<String> exercise_frequency;

    public User(String id, String username, String email, double height, double weight, int age, String gender) {
    this.userId = id;
    this.username = username;
    this.email = email;
    this.height = height;
    this.weight = weight;
    this.age = age;
    this.gender = gender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public WishList getWishList() {
        return wishList;
    }

    public void setWishList(WishList wishList) {
        this.wishList = wishList;
    }

    public FavoriteList getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(FavoriteList favoriteList) {
        this.favoriteList = favoriteList;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public ArrayList<String> getType() {
        return type;
    }

    public void setType(ArrayList<String> type) {
        type.add(0,"Mantain weight");
        type.add(1,"Slight loss weight");
        type.add(2,"Loss weight");
        type.add(3,"Extreme loss weight");
        this.type = type;
    }
    public ArrayList<String> getExercise_frequency() {
        return exercise_frequency;
    }

    public void setExercise_frequency(ArrayList<String> exercise_frequency) {
        exercise_frequency.add(0,"Sedentary: little or no exercise");
        exercise_frequency.add(1,"Exercise 1-3 times/week");
        exercise_frequency.add(2,"Exercise 4-5 times/week");
        exercise_frequency.add(3,"Daily exercise or intense exercise 3-4 times/week");
        exercise_frequency.add(4,"Intense exercise 6-7 times/week");
        exercise_frequency.add(5,"Very intense exercise daily, or physical job");
        this.exercise_frequency = exercise_frequency;
    }
}
