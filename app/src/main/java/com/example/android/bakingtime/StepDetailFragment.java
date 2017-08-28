package com.example.android.bakingtime;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingtime.data.Steps.Steps;
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
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;


    @BindView(R.id.step_video_url)
    TextView mStepVideoURLTextView;
    @BindView(R.id.step_thumbnail_url)
    TextView mStepThumbnailUrlTextView;
    @BindView(R.id.step_description)
    TextView mStepDescriptionTextView;
    @BindView(R.id.oven_image)
    ImageView mOvenImageView;

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

    private SimpleExoPlayer mExoPlayer;
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);

        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt("position");
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

//            Toast.makeText(getContext(), "position is " + mPosition, Toast.LENGTH_SHORT).show();
        }

        showOrHideThePlayer();

        return rootView;
    }

    private void showOrHideThePlayer() {
        // Checking if the step has a video
        if (mStepList.get(mPosition).getVideoURL().contains(getString(R.string.mp4))) {

            // If it does, showing the mPlayerView and removing the oven image as well as the videoURL
            mPlayerView.setVisibility(View.VISIBLE);
            mOvenImageView.setVisibility(View.GONE);
            mStepVideoURLTextView.setVisibility(View.GONE);

            // Initializing the player
            initializePlayer();

            // Initializing the mediaSession
            initializeMediaSession();

            // If the step description contains the word "Preheat"
        } else if (mStepList.get(mPosition).getDescription().contains(getString(R.string.preheat))) {
            // Showing the image of the oven and removing the mPlayerView
            mOvenImageView.setVisibility(View.VISIBLE);
            mPlayerView.setVisibility(View.GONE);

            // If there is no video or oven, removing both views and showing just the step description
        } else {
            mPlayerView.setVisibility(View.GONE);
            mStepVideoURLTextView.setVisibility(View.GONE);
        }
    }

    // Moving between recipes works!
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
        super.onSaveInstanceState(outState);
    }

    private void initializePlayer() {
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
//            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
        MediaSource mediaSource = buildMediaSource(uri);
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
            releasePlayer();
//            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
//            mMediaSession.setActive(false);
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
