package com.example.android.bakingtime.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Spinner;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.Recipes.Recipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The configuration screen for the {@link RecipeIngredientsWidgetProvider RecipeIngredientsWidgetProvider} AppWidget.
 */
public class RecipeIngredientsWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private AppWidgetManager widgetManager;
    private RemoteViews remoteViews;
    @Nullable
    @BindView(R.id.recipes_spinner)
    Spinner spinner;
    @Nullable
    @BindView(R.id.add_button)
    View btnCreate;


    public RecipeIngredientsWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.recipe_ingredients_widget_configure);
        ButterKnife.bind(this);


        // my code

        // Getting the recipeList from SharedPrefs
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("recipeList", null);
        Type type = new TypeToken<ArrayList<Recipe>>() {}.getType();
        final ArrayList<Recipe> recipeList = gson.fromJson(json, type);

        // Creating a new list which will contain only the recipes names
        ArrayList<String> recipes = new ArrayList<>();

        // Adding the name to the list
        for (int i = 0; i < recipeList.size(); i++) {
            recipes.add(recipeList.get(i).getName());
        }

        // Creating a new ArrayAdapter with each recipe name
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, recipes);
        // Setting the adapter to the spinner
        spinner.setAdapter(adapter);

        //initializing RemoteViews and AppWidgetManager
        widgetManager = AppWidgetManager.getInstance(this);
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.recipe_ingredients_widget);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        // Setting onClickListener on the addWidget button
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List ingredientsForSelectedRecipe;
                // Getting the ingredients for a selected recipe
                for (int i = 0; i < recipeList.size(); i++) {
                    if (spinner.getSelectedItemPosition() == i) {
                        ingredientsForSelectedRecipe = recipeList.get(i).getIngredients();
                        // Saving the ingredients for the selected recipe in SharedPreferences
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(ingredientsForSelectedRecipe);
                        editor.putString("ingredientsForSelectedRecipe", json);
                        editor.commit();
                    }
                }

                // Setting the intent that starts MyWidgetRemoteViewService
                Intent intent1 = new Intent(getApplicationContext(), MyWidgetRemoteViewsService.class);
                // Setting up the RemoteViews adapter to the RemoteViews object
                remoteViews.setRemoteAdapter(R.id.ingredients_list_widget, intent1);
                // Setting up the emptyView
                remoteViews.setEmptyView(R.id.ingredients_list_widget, R.id.empty_view);
                // Updating the widget
                widgetManager.updateAppWidget(mAppWidgetId, remoteViews);

                // Set the results
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });


    }
}

