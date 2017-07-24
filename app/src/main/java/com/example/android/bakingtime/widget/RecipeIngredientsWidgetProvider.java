package com.example.android.bakingtime.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.android.bakingtime.R;

import butterknife.BindView;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link RecipeIngredientsWidgetConfigureActivity RecipeIngredientsWidgetConfigureActivity}
 */
public class RecipeIngredientsWidgetProvider extends AppWidgetProvider {

    private String recipeName;
    @Nullable
    @BindView(R.id.recipe_name_in_widget)
    TextView mRecipeName;



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);



    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

