package com.example.android.bakingtime.data.Steps;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.MainActivity;
import com.example.android.bakingtime.R;
import com.example.android.bakingtime.StepDetailFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kolev on 15-Jun-17.
 */

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder> {

    private ArrayList<Steps> mRecipeStepsList;
    private Context mContext;
    private int selectedPosition = -1;

//    // trying the item selection highlight
//    private SparseBooleanArray selectedItems;


    public RecipeStepsAdapter(Context context, ArrayList<Steps> steps) {
        this.mContext = context;
        this.mRecipeStepsList = steps;
    }

    @Override
    public RecipeStepsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepsAdapter.ViewHolder holder, final int position) {
        holder.name.setText(mRecipeStepsList.get(position).getShortDescription());

    }


    @Override
    public int getItemCount() {
        return mRecipeStepsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.name)
        TextView name;
        public final View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
            mView.setOnClickListener(this);
            mView.setSelected(true);
        }

        @Override
        public void onClick(View view) {
//            Steps stepsList = mRecipeStepsList.get(getAdapterPosition());
            int position = getLayoutPosition();

            Bundle arguments = new Bundle();
            // works, passing the position to the fragment. have to figure out if that's the best way...
            arguments.putParcelableArrayList("stepList", mRecipeStepsList);
            arguments.putInt("position", position);

            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setArguments(arguments);

            FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();

            if (MainActivity.mTwoPane) {
                fragmentManager.beginTransaction()
                        .replace(R.id.recipe_step_detail_container_tablet, stepDetailFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.recipe_detail_container_phone, stepDetailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }


//    public void toggleSelection(int position) {
//        if (selectedItems.get(position, false)) {
//            selectedItems.delete(position);
//        } else {
//            selectedItems.put(position, true);
//        }
//        notifyItemChanged(position);
//    }
}
