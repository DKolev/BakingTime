package com.example.android.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.android.bakingtime.data.Ingredients.Ingredients;
import com.example.android.bakingtime.data.Recipes.Recipe;
import com.example.android.bakingtime.data.Steps.RecipeStepsAdapter;
import com.example.android.bakingtime.data.Steps.Steps;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static Recipe mRecipe;
    private ArrayList<Steps> mStepsList;
    private ArrayList<Ingredients> mIngredientsList;
    private RecipeStepsAdapter mRecipeStepsAdapter;

    public static final String ARG_RECIPE_ID = "recipe_id";

    @BindView(R.id.steps_list)
    RecyclerView mRecipeStepsListRecyclerView;
    @BindView(R.id.card_view_ingredients)
    CardView mIngredientsCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        // Getting the bundle from the MainActivity for the clicked recipe
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mRecipe = bundle.getParcelable(ARG_RECIPE_ID);
        }

        // Getting the steps list
        mStepsList = this.mRecipe.getSteps();

        // Getting the ingredients list
        mIngredientsList = this.mRecipe.getIngredients();

        if (MainActivity.mTwoPane) {
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_detail_container_tablet, recipeDetailFragment)
                    .commit();

        } else {

            // Creating a new Vertical LinearLayout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            // Setting it to the steps RecyclerView
            mRecipeStepsListRecyclerView.setLayoutManager(layoutManager);

            // Creating a new RecipeStepsAdapter
            mRecipeStepsAdapter = new RecipeStepsAdapter(this, mStepsList);
            mRecipeStepsAdapter.notifyDataSetChanged();
            // Setting the adapter on the RecyclerView
            mRecipeStepsListRecyclerView.setAdapter(mRecipeStepsAdapter);

            // Adding a divider between the recipe steps
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecipeStepsListRecyclerView.getContext(),
                    LinearLayoutManager.VERTICAL);
            mRecipeStepsListRecyclerView.addItemDecoration(dividerItemDecoration);

            // Setting onClickListener on the CardView
            mIngredientsCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // this is to replace the old fragment and not to start a new activity --WORKS--
                    Bundle arguments = new Bundle();
                    // Packing the ingredients list as a parcelable array list and sending it to the
                    // IngredientsFragment as an arguments
                    arguments.putParcelableArrayList("ingredients", mIngredientsList);

                    // Creating a new IngredientsFragment
                    IngredientsFragment ingredientsFragment = new IngredientsFragment();
                    // Setting the passed arguments
                    ingredientsFragment.setArguments(arguments);

                    // Creating a new FragmentManager
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    // Replacing the container with the recipe details with one showing all
                    // the ingredients necessary ingredients
                    fragmentManager.beginTransaction()
                            .add(R.id.recipe_detail_container_phone, ingredientsFragment)
                            .addToBackStack(null)
                            .commit();

                }
            });

        }
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
