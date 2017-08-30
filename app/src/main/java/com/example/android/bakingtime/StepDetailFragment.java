package com.example.android.bakingtime;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingtime.data.Steps.Steps;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Kolev on 17-Jun-17.
 */

public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener {

    private String LOG_TAG = StepDetailFragment.class.getSimpleName();
    private ArrayList<Steps> mStepList;
    private int mPosition;
    private long mExoPlayerPosition;
    private MediaSessionCompat mMediaSession;
    private SimpleExoPlayer mExoPlayer;
    private PlaybackStateCompat.Builder mStateBuilder;


    @BindView(R.id.step_video_url)
    TextView mStepVideoURLTextView;
    @BindView(R.id.step_thumbnail_url)
    TextView mStepThumbnailUrlTextView;
    @BindView(R.id.step_description)
    TextView mStepDescriptionTextView;
    @BindView(R.id.recipe_step_image)
    ImageView mRecipeStepImage;

    @Nullable
    @BindView(R.id.step_number)
    TextView mStepNumber;
    @Nullable
    @BindView(R.id.total_steps)
    TextView mTotalSteps;
    @Nullable
    @BindView(R.id.steps_separator)
    TextView mStepsSeparator;

    @Nullable
    @BindView(R.id.previous_step_button)
    Button mPreviousStep;
    @Nullable
    @BindView(R.id.next_step_button)
    Button mNextStepButton;

    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("stepList")) {

            Bundle bundle = getArguments();
            if (bundle != null) {
                mStepList = bundle.getParcelableArrayList("stepList");
            }
        }

        // works, getting the position
        if (getArguments().containsKey("position")) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                mPosition = bundle.getInt("position");
            }
        }

        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);

        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt("position");
            mExoPlayerPosition = savedInstanceState.getLong("playerPosition", C.TIME_UNSET);
            if (mStepNumber != null) {
                mStepNumber.setText(String.valueOf(mPosition));
            }
            if (mTotalSteps != null) {
                mTotalSteps.setText(String.valueOf(mStepList.size() - 1));
            }
            mStepDescriptionTextView.setText(savedInstanceState.getString("description"));
            mStepVideoURLTextView.setText(savedInstanceState.getString("videoUrl"));
        } else {

            mStepDescriptionTextView.setText(mStepList.get(mPosition).getDescription());
            mStepVideoURLTextView.setText(mStepList.get(mPosition).getVideoURL());
            mStepThumbnailUrlTextView.setText(mStepList.get(mPosition).getThumbnailURL());

            if (mStepNumber != null) {
                mStepNumber.setText(String.valueOf(mPosition));
            }
            if (mTotalSteps != null) {
                mTotalSteps.setText(String.valueOf(mStepList.size() - 1));
            }

        }

        showOrHideThePlayer();

        return rootView;
    }

    /**
     * This method handles the visibility of the recipe image view and the player view for each step
     * based on what's available in the JSON data
     */
    private void showOrHideThePlayer() {

        String thumbnailUrl = mStepThumbnailUrlTextView.getText().toString();
        String videoUrl = mStepList.get(mPosition).getVideoURL();

        // If there is a step image for this step in the JSON data but no video
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            // Loading the image using Picasso
            Picasso.with(getContext()).load(thumbnailUrl).into(mRecipeStepImage);
            // Making the image view visible and hiding the player and the text view
            mRecipeStepImage.setVisibility(View.VISIBLE);
            mPlayerView.setVisibility(View.GONE);
            mStepVideoURLTextView.setVisibility(View.GONE);
            // If there is a video as well as a step image, showing both views
            if (mStepList.get(mPosition).getVideoURL().contains(getString(R.string.mp4))) {
                mPlayerView.setVisibility(View.VISIBLE);
                mRecipeStepImage.setVisibility(View.VISIBLE);
                mStepVideoURLTextView.setVisibility(View.GONE);

                // Initializing the player
                initializePlayer();

                // Initializing the mediaSession
                initializeMediaSession();
            }
            // If the step description contains the word "Preheat" showing the
            // image of the oven and removing the mPlayerView
            if (mStepList.get(mPosition).getDescription().contains(getString(R.string.preheat))) {
                mRecipeStepImage.setImageResource(R.drawable.oven);
                mRecipeStepImage.setVisibility(View.VISIBLE);
                mPlayerView.setVisibility(View.GONE);
            }
        } else {
            // If there isn't a step image in the JSON data I'm checking if the step has just a video
            if (!TextUtils.isEmpty(videoUrl)) {

                // If it does, showing the mPlayerView and removing the step image as well as the videoURL
                mPlayerView.setVisibility(View.VISIBLE);
                mRecipeStepImage.setVisibility(View.GONE);
                mStepVideoURLTextView.setVisibility(View.GONE);

                // Initializing the player
                initializePlayer();

                // Initializing the mediaSession
                initializeMediaSession();

            } else if (TextUtils.isEmpty(videoUrl)) {
                // If there is no video url but an image url
                if (!TextUtils.isEmpty(thumbnailUrl)) {
                    // Loading the image using Picasso
                    Picasso.with(getContext()).load(thumbnailUrl).into(mRecipeStepImage);
                    // Hiding the player view
                    mPlayerView.setVisibility(View.GONE);
                } else if (mStepList.get(mPosition).getDescription().contains("Preheat")){
                    // If there is a word "Preheat" in the description, show the oven image
                    // The idea is this image to show only with a description where it is appropriate
                    // Hide the player
                    mPlayerView.setVisibility(View.GONE);
                    // Load the oven image
                    mRecipeStepImage.setImageResource(R.drawable.oven);
                    // Make the view visible
                    mRecipeStepImage.setVisibility(View.VISIBLE);
                } else {
                    // Hide the player view
                    mPlayerView.setVisibility(View.GONE);
                }
            }
        }
    }

    // Moving between recipes works!

    /**
     * Shows the next step when the next button is clicked
     */
    @Optional
    @OnClick(R.id.next_step_button)
    public void nextStep() {
        if (mPosition < mStepList.size() - 1) {
            mPosition++;
            if (mStepNumber != null) {
                mStepNumber.setText(String.valueOf(mPosition));
            }
            mStepVideoURLTextView.setText(mStepList.get(mPosition).getVideoURL());


        } else {
            Toast.makeText(getContext(), "That's the last step", Toast.LENGTH_SHORT).show();
        }
        // Releasing the player with the previous video
        releasePlayer();
        // Showing or hiding the player based on if there's a videoUrl to load
        showOrHideThePlayer();

        // Showing the videoUrl (for now) and step description
        mStepVideoURLTextView.setText(mStepList.get(mPosition).getVideoURL());
        mStepDescriptionTextView.setText(mStepList.get(mPosition).getDescription());

    }

    /**
     * Shows the previous step when the previous button is clicked
     */
    @Optional
    @OnClick(R.id.previous_step_button)
    public void previousStep() {
        if (mPosition == 0) {
            Toast.makeText(getContext(), "That's the first step", Toast.LENGTH_SHORT).show();
        } else {
            mPosition--;
            if (mStepNumber != null) {
                mStepNumber.setText(String.valueOf(mPosition));
            }
            mStepVideoURLTextView.setText(mStepList.get(mPosition).getVideoURL());
        }
        // Releasing the player with the previous video
        releasePlayer();

        // Showing or hiding the player based on if there's a videoUrl to load
        showOrHideThePlayer();

        mStepVideoURLTextView.setText(mStepList.get(mPosition).getVideoURL());
        mStepDescriptionTextView.setText(mStepList.get(mPosition).getDescription());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", mPosition);
        outState.putString("description", mStepDescriptionTextView.getText().toString());
        outState.putString("videoUrl", mStepVideoURLTextView.getText().toString());
        outState.putLong("playerPosition", mExoPlayerPosition);
        super.onSaveInstanceState(outState);
    }

    /**
     * This method initializes the player
     */
    private void initializePlayer() {
        if (mExoPlayer != null) {
            return;
        }
        // Create an instance of the ExoPlayer
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()),
                trackSelector, loadControl);
        mPlayerView.setPlayer(mExoPlayer);
        mExoPlayer.setPlayWhenReady(true);

        // Set the ExoPlayer.EventListener to this fragment
        mExoPlayer.addListener(this);

        // Prepare the MediaSource
        Uri uri = Uri.parse(mStepVideoURLTextView.getText().toString());
        MediaSource mediaSource = buildMediaSource(uri);

        // If the video is paused on rotation, skip to this position
        if (mExoPlayerPosition != C.TIME_UNSET) {
            mExoPlayer.seekTo(mExoPlayerPosition);
        }
        mExoPlayer.prepare(mediaSource, true, false);
        mExoPlayer.prepare(mediaSource);


    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (mExoPlayer != null) {
                mExoPlayerPosition = mExoPlayer.getCurrentPosition();
                releasePlayer();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    // works, tested with playback controls on my headphones
    private void initializeMediaSession() {
        // Create a media session
        mMediaSession = new MediaSessionCompat(getContext(), LOG_TAG);

        // Enable callbacks
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Preventing MediaButtons to restart the player when app is not visible
        mMediaSession.setMediaButtonReceiver(null);

        // Set the initial playback state
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        mMediaSession.setCallback(new MySessionCallback());

        // Start the media session
        mMediaSession.setActive(true);
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (mExoPlayer == null || mMediaSession == null) {
            return;
        }
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            // We are playing
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            // We are paused
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }

        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}
