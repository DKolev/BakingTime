package com.example.android.bakingtime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingtime.data.Recipes.Recipe;
import com.example.android.bakingtime.data.Recipes.RecipeAdapter;
import com.example.android.bakingtime.data.Recipes.RequestInterface;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String BASE_URL_FOR_RETROFIT = "https://d17h27t6h515a5.cloudfront.net";
    private String RECIPES_LIST = "recipesList";

    public static boolean mTwoPane;
    private ArrayList<Recipe> mRecipeList;
    private RecipeAdapter mRecipeAdapter;
    @BindView(R.id.recipe_list)
    RecyclerView mRecipeRecyclerView;
    @BindView(R.id.main_activity_empty_view)
    TextView mErrorMessageTextView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mySwipeRefreshLayout;

    ConnectivityManager mConnectivityManager;
    NetworkInfo mNetworkInfo;


    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (savedInstanceState != null) {
            mRecipeList = savedInstanceState.getParcelableArrayList(RECIPES_LIST);
        }

        mTwoPane = getApplicationContext().getResources().getBoolean(R.bool.isTablet);


        if (isTablet(getBaseContext())) {
            // Creating a new Grid Layout manager
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
            // Setting the layout manager to the mRecipeRecyclerView
            mRecipeRecyclerView.setLayoutManager(layoutManager);


        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
            // Creating a new Vertical LinearLayout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            // Setting the layout manager to the mRecipeRecyclerView
            mRecipeRecyclerView.setLayoutManager(layoutManager);
        }

        // Getting a reference to the ConnectivityManager to check state of network connectivity
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Getting details on the currently active default data network
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        // If there is a network connection, load the recipes.
        if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
            // Show the recipes
            showRecipes();
        } else {
            // If there is no connection, show the error message
            showErrorMessage();
        }

        // trying swipe-to-refresh. Not quite there yet
        mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecipeList();

            }

        });

    }

    /**
     * This method refreshes the recipe list when swipe-refresh gesture is invoked
     */
    private void refreshRecipeList() {
        // Getting a reference to the ConnectivityManager to check state of network connectivity
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Getting details on the currently active default data network
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        // If there is a network connection, load the recipes.
        if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
            // Show the recipes
                showRecipes();
        } else {
            // If there is no connection, show the error message
            showErrorMessage();
        }

        if (mySwipeRefreshLayout != null && mySwipeRefreshLayout.isRefreshing()) {
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }


    /**
     * A method that loads the recipes using Retrofit
     */
    private void loadRecipesFromJSON() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_FOR_RETROFIT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpProvider.getOkHttpInstance())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        Call<List<Recipe>> call = requestInterface.getJSONRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {

                // Getting the recipeList
                mRecipeList = (ArrayList<Recipe>) response.body();

                // Saving the recipeList to the SharedPreferences
                // https://stackoverflow.com/questions/22984696/storing-array-list-object-in-sharedpreferences
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();

                String json = gson.toJson(mRecipeList);
                editor.putString("recipeList", json);
                editor.apply();

                // Setting the list to a new RecipeAdapter
                mRecipeAdapter = new RecipeAdapter(getBaseContext(), mRecipeList);
                mRecipeAdapter.notifyDataSetChanged();
                // Setting the adapter to the RecyclerView
                mRecipeRecyclerView.setAdapter(mRecipeAdapter);

            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong while fetching data. Please try again.", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(RECIPES_LIST, mRecipeList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mRecipeList = savedInstanceState.getParcelableArrayList(RECIPES_LIST);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /***
     * This method loads the recipes using Retrofit2 (by default) or uses the mRecipeList if it is
     * already populated in onSaveInstanceState
     */

    private void showRecipes() {
        if (mRecipeList != null) {
            // Setting the error message to be invisible
            mErrorMessageTextView.setVisibility(View.INVISIBLE);
            // Creating a new RecipeAdapter
            mRecipeAdapter = new RecipeAdapter(getBaseContext(), mRecipeList);
            mRecipeAdapter.notifyDataSetChanged();
            // Setting the adapter to the RecyclerView
            mRecipeRecyclerView.setAdapter(mRecipeAdapter);
        } else {
            // Setting the error message to be invisible
            mErrorMessageTextView.setVisibility(View.INVISIBLE);
            mRecipeRecyclerView.setVisibility(View.VISIBLE);
            // Loading the recipes using Retrofit2
            loadRecipesFromJSON();
        }
    }

    private void showErrorMessage() {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mRecipeRecyclerView.setVisibility(View.INVISIBLE);
    }

}
