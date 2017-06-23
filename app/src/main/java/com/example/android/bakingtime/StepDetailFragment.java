package com.example.android.bakingtime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingtime.data.Steps.Steps;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kolev on 17-Jun-17.
 */

public class StepDetailFragment extends Fragment {

    private Steps mStep;
    private int mPosition;

    @BindView(R.id.step_video_url)
    TextView mStepVideoURLTextView;
    @BindView(R.id.step_thumbnail_url)
    TextView mStepThumbnailUrlTextView;
    @BindView(R.id.step_description)
    TextView mStepDescriptionTextView;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("step")) {

            Bundle bundle = getArguments();
            if (bundle != null) {
                mStep = bundle.getParcelable("step");
            }
        }

        // works, getting the position
        if (getArguments().containsKey("position")) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                mPosition = bundle.getInt("position");
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);

        ButterKnife.bind(this, rootView);

        mStepDescriptionTextView.setText(mStep.getDescription());
        mStepVideoURLTextView.setText(mStep.getVideoURL());
        mStepThumbnailUrlTextView.setText(mStep.getThumbnailURL());

        Toast.makeText(getContext(), "position is " + mPosition, Toast.LENGTH_SHORT).show();

        return rootView;
    }

    // TODO (in a phone mode)
    // have to come up with a method to move to the next step when NEXT button is pressed
    // and to previous step when the PREVIOUS step is pressed

    // TODO (in a tablet mode)
    // the clicked step should change it's background
}
