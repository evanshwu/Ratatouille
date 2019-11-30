package com.cmu.ratatouille.managers;

import com.cmu.ratatouille.exceptions.AppException;
import com.cmu.ratatouille.models.MealHistory;
import com.cmu.ratatouille.models.MealPlan;
import com.cmu.ratatouille.models.Recipe;
import com.cmu.ratatouille.models.User;
import com.cmu.ratatouille.utils.CalorieMultiplier;
import com.cmu.ratatouille.utils.MongoPool;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MealManager extends Manager {
    public static MealManager _self;
    private MongoCollection<Document> recipeCollection;
    private MongoCollection<Document> userCollection;

    public MealManager() {
        this.recipeCollection = MongoPool.getInstance().getCollection("Recipes");
        this.userCollection = MongoPool.getInstance().getCollection("users");
    }

    // Push to history and add new meal plan
    public void pushMeal(User user) throws AppException{
        try{
            ArrayList<MealHistory> history = user.getMealHistories();
            MealHistory newRecord = new MealHistory();

            // Get meal plan
            double bmr = CalorieMultiplier.getBMR(user.getGender(), (int)user.getWeight(), (int)user.getHeight(), user.getAge());
            MealPlan plan = recommendMeal(CalorieMultiplier.getCalories(bmr, user.getExercise_frequency()));

            if(history!=null && history.size()>0){
                ArrayList<MealHistory> _history = new ArrayList<>();
                newRecord.setDate(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()));
                newRecord.setMealPlan(plan);
                _history.add(newRecord);
                _history.addAll(history);

                user.setMealHistories(_history);
            }else{
                newRecord.setDate(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()));
                newRecord.setMealPlan(plan);
                history.add(newRecord);

                user.setMealHistories(history);
            }

            UserManager.getInstance().updateUser(user);
        }catch(Exception ex){
            throw handleException("Error pushing new meal record!", ex);
        }
    }

    public MealPlan recommendMeal(Double calorie) throws AppException{
        try{
            MealPlan plan = new MealPlan();
            ArrayList<Recipe> recipes = RecipeManager.getInstance().getRecipeWithFiltersAndSortings(null, calorie+100+"", calorie-100+"", "", "", "");

            // Randomly selects recipes from the list
            // Generate 21 random indexes without overlapping
            Random random = new Random();
            int[] indexes = new int[21];
            Set<Integer> used = new HashSet<>();
            for(int i=0;i<21;i++){
                int newRandom;
                do{
                    newRandom = random.nextInt(recipes.size()+1);
                }while(used.contains(newRandom));
                indexes[i] = newRandom;
                used.add(newRandom);
            }

            int day = 1;
            for(int j=0;j<21;j+=3){
                ArrayList<Recipe> _recipe = new ArrayList<>();
                _recipe.add(recipes.get(indexes[j]));
                _recipe.add(recipes.get(indexes[j+1]));
                _recipe.add(recipes.get(indexes[j+2]));

                switch (day){
                    case 1:
                        plan.setMonday(_recipe);
                        day++;
                        break;
                    case 2:
                        plan.setTuesday(_recipe);
                        day++;
                        break;
                    case 3:
                        plan.setWednesday(_recipe);
                        day++;
                        break;
                    case 4:
                        plan.setThursday(_recipe);
                        day++;
                        break;
                    case 5:
                        plan.setFriday(_recipe);
                        day++;
                        break;
                    case 6:
                        plan.setSaturday(_recipe);
                        day++;
                        break;
                    case 7:
                        plan.setSunday(_recipe);
                        day++;
                        break;
                }
            }
            return plan;
        }catch(Exception ex){
            throw handleException("Error calculating meal plans!", ex);
        }


    }


}
