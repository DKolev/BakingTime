package com.example.android.bakingtime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.data.Ingredients.Ingredients;
import com.example.android.bakingtime.data.Recipes.Recipe;
import com.example.android.bakingtime.data.Steps.RecipeStepsAdapter;
import com.example.android.bakingtime.data.Steps.Steps;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_RECIPE_ID = "recipe_id";

    /**
     * The recipe content this fragment is presenting.
     */
    private Recipe mRecipe;
    private ArrayList<Steps> mStepsList;
    private ArrayList<Ingredients> mIngredientsList;
    private RecipeStepsAdapter mRecipeStepsAdapter;

    @BindView(R.id.steps_list)
    RecyclerView mRecipeStepsListRecyclerView;
    @BindView(R.id.card_view_ingredients)
    CardView mIngredientsCardView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecipe = RecipeDetailActivity.mRecipe;
        // Getting the steps list
        mStepsList = this.mRecipe.getSteps();

        // Getting the ingredients list
        mIngredientsList = this.mRecipe.getIngredients();

        setRetainInstance(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);
        ButterKnife.bind(this, rootView);

        // Creating a new Vertical LinearLayout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        // Setting it to the steps list RecyclerView
        mRecipeStepsListRecyclerView.setLayoutManager(layoutManager);

        // Creating a new RecipeStepsAdapter
        mRecipeStepsAdapter = new RecipeStepsAdapter(getContext(), mStepsList);
        mRecipeStepsAdapter.notifyDataSetChanged();
        // Setting the adapter to the steps list RecyclerView
        mRecipeStepsListRecyclerView.setAdapter(mRecipeStepsAdapter);

        // Adding a divider between the recipe steps
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecipeStepsListRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecipeStepsListRecyclerView.addItemDecoration(dividerItemDecoration);

        if (MainActivity.mTwoPane) {
            // Setting an onClickListener on the Ingredient card
            mIngredientsCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // this is to replace the old fragment and not to start a new activity --WORKS--
                    // Packing the ingredients list as a parcelable array list and sending it to the
                    // IngredientsFragment as an arguments
                    Bundle arguments = new Bundle();
                    arguments.putParcelableArrayList("ingredients", mIngredientsList);

                    // Creating a new IngredientsFragment
                    IngredientsFragment ingredientsFragment = new IngredientsFragment();
                    // Setting the passed arguments
                    ingredientsFragment.setArguments(arguments);

                    // Creating a new FragmentManager
                    FragmentManager fragmentManager = getFragmentManager();

                    fragmentManager.beginTransaction()
                            .replace(R.id.recipe_step_detail_container_tablet, ingredientsFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        } else {

            // Setting an onClickListener on the Ingredient card
            mIngredientsCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // this is to replace the old fragment and not to start a new activity --WORKS--
                    // Packing the ingredients list as a parcelable array list and sending it to the
                    // IngredientsFragment as an arguments
                    Bundle arguments = new Bundle();
                    arguments.putParcelableArrayList("ingredients", mIngredientsList);

                    // Creating a new IngredientsFragment
                    IngredientsFragment ingredientsFragment = new IngredientsFragment();
                    // Setting the passed arguments
                    ingredientsFragment.setArguments(arguments);

                    // Creating a new FragmentManager
                    FragmentManager fragmentManager = getFragmentManager();

                    fragmentManager.beginTransaction()
                            .replace(R.id.recipe_detail_container_phone, ingredientsFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }


        return rootView;
    }

}

