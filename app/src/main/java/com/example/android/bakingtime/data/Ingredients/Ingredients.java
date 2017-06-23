package com.example.android.bakingtime.data.Ingredients;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kolev on 08-Jun-17.
 */

public class Ingredients implements Parcelable {

    private String quantity;
    private String measure;
    private String ingredient;

    public Ingredients(String ingredientQuantity, String ingredientMeasure, String ingredientName) {
        quantity = ingredientQuantity;
        measure = ingredientMeasure;
        ingredient = ingredientName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.quantity);
        dest.writeString(this.measure);
        dest.writeString(this.ingredient);
    }

    protected Ingredients(Parcel in) {
        this.quantity = in.readString();
        this.measure = in.readString();
        this.ingredient = in.readString();
    }

    public static final Parcelable.Creator<Ingredients> CREATOR = new Parcelable.Creator<Ingredients>() {
        @Override
        public Ingredients createFromParcel(Parcel source) {
            return new Ingredients(source);
        }

        @Override
        public Ingredients[] newArray(int size) {
            return new Ingredients[size];
        }
    };
}
