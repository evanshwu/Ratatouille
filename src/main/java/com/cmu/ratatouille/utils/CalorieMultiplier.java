package com.cmu.ratatouille.utils;

import com.cmu.ratatouille.models.Gender;

public class CalorieMultiplier {

    /*
Sedentary : little or no exercise = bmr * 1.2
Light: exercise 1-3 times/week = bmr * 1.37
Moderate: exercise 4-5 times/week = bmr * 1.46
Active: daily exercise or intense exercise 3-4 times/week = bmr * 1.55
Very active: intense exercise 6-7 times/week = bmr * 1.72
Extra active: very intense exercise daily, or physical job = bmr * 1.9
*/
    public static double getCalories(double baseCalorie, String preferences){
        double result = 0;
        switch(preferences){
            case "sedentary":
                result = baseCalorie * 1.2;
                break;
            case "light":
                result = baseCalorie * 1.37;
                break;
            case "moderate":
                result = baseCalorie * 1.46;
                break;
            case "active":
                result = baseCalorie * 1.55;
                break;
            case "very active":
                result = baseCalorie * 1.72;
                break;
            case "extra active":
                result = baseCalorie * 1.9;
                break;
        }
        return result;
    }

    public static double getBMR(String gender, int weight, int height, int age){
        if(gender.equals("male"))
            return 10*weight + 6.25*height - 5*age + 5;
        return 10*weight + 6.25*height - 5*age - 161;
    }
}
