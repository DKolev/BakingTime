package com.example.android.bakingtime.data.Recipes;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.bakingtime.data.Ingredients.Ingredients;
import com.example.android.bakingtime.data.Steps.Steps;

import java.util.ArrayList;

/**
 * Created by Kolev on 06-Jun-17.
 */

public class Recipe implements Parcelable {

    private String name;
    private ArrayList<Ingredients> ingredients;
    private ArrayList<Steps> steps;
    private int servings;
    private String image;

    public Recipe(String recipeName, ArrayList<Ingredients> recipeIngredients,
                  ArrayList<Steps> recipeSteps, int recipeServings, String recipeImage) {
        this.name = recipeName;
        this.ingredients = recipeIngredients;
        this.steps = recipeSteps;
        this.servings = recipeServings;
        this.image = recipeImage;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredients> getIngredients() {
        return ingredients;
    }

    public ArrayList<Steps> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeList(this.ingredients);
        dest.writeList(this.steps);
        dest.writeInt(this.servings);
        dest.writeString(this.image);
    }

    protected Recipe(Parcel in) {
        this.name = in.readString();
        this.ingredients = new ArrayList<>();
        in.readList(this.ingredients, Ingredients.class.getClassLoader());
        this.steps = new ArrayList<>();
        in.readList(this.steps, Steps.class.getClassLoader());
        this.servings = in.readInt();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
