package com.example.android.bakingtime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.data.Ingredients.Ingredients;
import com.example.android.bakingtime.data.Ingredients.IngredientsAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kolev on 17-Jun-17.
 */

public class IngredientsFragment extends Fragment {

    public static final String ARG_INGREDIENT_ID = "ingredient_id";

    private ArrayList<Ingredients> mIngredientsList;
    private IngredientsAdapter mIngredientAdapter;
    @BindView(R.id.ingredient_list)
    RecyclerView mIngredientsRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("ingredients")) {
//             Load the dummy content specified by the fragment
//             arguments. In a real-world scenario, use a Loader
//             to load content from a content provider.
            Bundle bundle = getArguments();
            if (bundle != null) {
                mIngredientsList = bundle.getParcelableArrayList("ingredients");
            }
        }

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
        ButterKnife.bind(this, rootView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mIngredientsRecyclerView.setLayoutManager(layoutManager);

        mIngredientAdapter = new IngredientsAdapter(getContext(), mIngredientsList);
        mIngredientAdapter.notifyDataSetChanged();
        mIngredientsRecyclerView.setAdapter(mIngredientAdapter);

        // Adding a divider between the recipe steps
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mIngredientsRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mIngredientsRecyclerView.addItemDecoration(dividerItemDecoration);

        return rootView;

    }
}
