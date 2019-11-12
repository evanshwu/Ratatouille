package com.cmu.ratatouille.utils;

public class CalorieCalculator {
    public static int calculateBmr(String gender, int weight, int height, int age){
        if(gender.equals("m"))
            return (int)(10 * weight + 6.25 * height - 5 * age +5);
        return (int)(10 * weight + 6.25 * height - 5 * age - 161);
    }
}
