package com.example.android.bakingtime.data.Recipes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.RecipeDetailActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kolev on 06-Jun-17.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private ArrayList<Recipe> mRecipeList;
    private Context mContext;
    private int selected_position = 0;


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
        holder.name.setText(mRecipeList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_name)
        TextView name;
        //        @BindView(R.id.recipe_image)
//        ImageView image;
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

