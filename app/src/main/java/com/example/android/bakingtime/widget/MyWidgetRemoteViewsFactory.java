package com.example.android.bakingtime.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.Ingredients.Ingredients;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Kolev on 21-Jul-17.
 */

public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Intent mIntent;
    private ArrayList<Ingredients> mIngredientsList;

    public MyWidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        // Getting the ingredients for the selectedRecipe from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("ingredientsForSelectedRecipe", null);
        Type type = new TypeToken<ArrayList<Ingredients>>() {}.getType();
        mIngredientsList = gson.fromJson(json, type);

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mIngredientsList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mIngredientsList == null) {
            return null;
    }
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_list_content);

        remoteViews.setTextViewText(R.id.ingredient_name, mIngredientsList.get(position).getIngredient());
        remoteViews.setTextViewText(R.id.ingredient_quantity, mIngredientsList.get(position).getQuantity());
        remoteViews.setTextViewText(R.id.ingredient_measure, mIngredientsList.get(position).getMeasure());

        return remoteViews;
    }



    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
