package com.cmu.ratatouille.models;

import java.util.ArrayList;

public class MealPlan {
    ArrayList<Recipe> Monday;
    ArrayList<Recipe> Tuesday;
    ArrayList<Recipe> Wednesday;
    ArrayList<Recipe> Thursday;
    ArrayList<Recipe> Friday;
    ArrayList<Recipe> Saturday;
    ArrayList<Recipe> Sunday;

    public ArrayList<Recipe> getMonday() {
        return Monday;
    }

    public void setMonday(ArrayList<Recipe> monday) {
        Monday = monday;
    }

    public ArrayList<Recipe> getTuesday() {
        return Tuesday;
    }

    public void setTuesday(ArrayList<Recipe> tuesday) {
        Tuesday = tuesday;
    }

    public ArrayList<Recipe> getWednesday() {
        return Wednesday;
    }

    public void setWednesday(ArrayList<Recipe> wednesday) {
        Wednesday = wednesday;
    }

    public ArrayList<Recipe> getThursday() {
        return Thursday;
    }

    public void setThursday(ArrayList<Recipe> thursday) {
        Thursday = thursday;
    }

    public ArrayList<Recipe> getFriday() {
        return Friday;
    }

    public void setFriday(ArrayList<Recipe> friday) {
        Friday = friday;
    }

    public ArrayList<Recipe> getSaturday() {
        return Saturday;
    }

    public void setSaturday(ArrayList<Recipe> saturday) {
        Saturday = saturday;
    }

    public ArrayList<Recipe> getSunday() {
        return Sunday;
    }

    public void setSunday(ArrayList<Recipe> sunday) {
        Sunday = sunday;
    }
}
