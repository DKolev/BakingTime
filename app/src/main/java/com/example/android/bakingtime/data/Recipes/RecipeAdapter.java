package com.example.android.bakingtime.data.Recipes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.RecipeDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kolev on 06-Jun-17.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private ArrayList<Recipe> mRecipeList;
    private Context mContext;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipe) {
        this.mRecipeList = recipe;
        this.mContext = context;
    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_content, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeAdapter.ViewHolder holder, final int position) {
        String recipeName = mRecipeList.get(position).getName();
        holder.name.setText(recipeName);

        String recipeImageUrl = mRecipeList.get(position).getImage();
        if (recipeImageUrl.length() > 0) {
            Picasso.with(mContext).load(recipeImageUrl).into(holder.mRecipeImageSmall);
        } else {

            if (recipeName.contains("Nutella Pie")) {
                holder.mRecipeImageSmall.setImageResource(R.drawable.nutella_pie);
            } else if (recipeName.contains("Brownies")) {
                holder.mRecipeImageSmall.setImageResource(R.drawable.brownies);
            } else if (recipeName.contains("Yellow Cake")) {
                holder.mRecipeImageSmall.setImageResource(R.drawable.yellow_cake);
            } else if (recipeName.contains("Cheesecake")) {
                holder.mRecipeImageSmall.setImageResource(R.drawable.cheesecake);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_name)
        TextView name;
        @BindView(R.id.recipe_image_small)
        ImageView mRecipeImageSmall;
        public final View mView;


        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
            mView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Recipe recipe = mRecipeList.get(getAdapterPosition());
            Bundle bundle = new Bundle();
            bundle.putParcelable(RecipeDetailActivity.ARG_RECIPE_ID, recipe);

            Intent intent = new Intent(view.getContext(), RecipeDetailActivity.class);
            intent.putExtras(bundle);
            view.getContext().startActivity(intent);

        }

    }

}

