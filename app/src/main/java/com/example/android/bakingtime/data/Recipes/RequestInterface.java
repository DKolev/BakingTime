package com.example.android.bakingtime.data.Recipes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Kolev on 06-Jun-17.
 */

public interface RequestInterface {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getJSONRecipes ();
}
