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
import java.util.Random;

public class MealManager extends Manager {
    public static MealManager _self;
    private MongoCollection<Document> recipeCollection;
    private MongoCollection<Document> userCollection;

    public MealManager() {
        this.recipeCollection = MongoPool.getInstance().getCollection("Recipes");
        this.userCollection = MongoPool.getInstance().getCollection("users");
    }

    public static MealManager getInstance(){
        if (_self == null)
            _self = new MealManager();
        return _self;
    }

    // Push to history and add new meal plan
    public void pushMeal(User user) throws AppException{
        try{
            System.out.println("Inside push meal");
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
                history = new ArrayList<>();
                history.add(newRecord);

                user.setMealHistories(history);
            }

            UserManager.getInstance().updateUser(null, user, true);
        }catch(Exception ex){
            throw handleException("Error pushing new meal record!", ex);
        }
    }

    public MealPlan recommendMeal(Double calorie) throws AppException{
        try{
            MealPlan plan = new MealPlan();
            ArrayList<Recipe> recipes = RecipeManager.getInstance().getRecipeWithFiltersAndSortings(null, calorie-100+"", calorie+100+"", null, null, null);
            System.out.println("Got recipe size="+recipes.size());
            // Randomly selects recipes from the list
            // Generate 21 random indexes without overlapping
            Random random = new Random();
            int[] indexes = new int[21];
            ArrayList<Integer> used = new ArrayList<>();
            for(int i=0;i<21;i++){
                //int newRandom = random.nextInt(recipes.size()+1);
                int newRandom = getRandom(0, recipes.size()-1);
//                System.out.println("Random generated: " + newRandom);
                indexes[i] = newRandom;
                used.add(newRandom);
            }

            int day = 1;
            for(int j=0;j<21;j+=3){
//                System.out.println("day "+day);
                ArrayList<Recipe> _recipe = new ArrayList<>();
                _recipe.add(recipes.get(indexes[j]));
                _recipe.add(recipes.get(indexes[j+1]));
                _recipe.add(recipes.get(indexes[j+2]));
//                System.out.println("recipe="+recipes.get(indexes[j]).getRecipeName());
//                System.out.println("recipe="+recipes.get(indexes[j+1]).getRecipeName());
//                System.out.println("recipe="+recipes.get(indexes[j+2]).getRecipeName());

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

    public int getRandom(int min, int max){
        return (int)(Math.random() * ((max - min) + 1)) + min;
    }


}
