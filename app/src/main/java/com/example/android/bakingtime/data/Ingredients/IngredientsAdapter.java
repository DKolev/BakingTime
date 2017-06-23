package com.example.android.bakingtime.data.Ingredients;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kolev on 16-Jun-17.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private ArrayList<Ingredients> mIngredientsList;
    private Context mContext;

    public IngredientsAdapter(Context context, ArrayList<Ingredients> ingredients) {
        this.mIngredientsList = ingredients;
        this.mContext = context;
    }

    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list_content, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IngredientsAdapter.ViewHolder holder, final int position) {
        holder.ingredientName.setText(mIngredientsList.get(position).getIngredient());
        holder.ingredientQuantity.setText(mIngredientsList.get(position).getQuantity());
        holder.ingredientMeasure.setText(mIngredientsList.get(position).getMeasure());
    }

    @Override
    public int getItemCount() {
        return mIngredientsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ingredient_name)
        TextView ingredientName;
        @BindView(R.id.ingredient_quantity)
        TextView ingredientQuantity;
        @BindView(R.id.ingredient_measure)
        TextView ingredientMeasure;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
