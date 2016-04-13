package com.zaidhuda.rememberthecolor.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;

import com.zaidhuda.rememberthecolor.R;
import com.zaidhuda.rememberthecolor.objects.ActivitySwipeDetector;
import com.zaidhuda.rememberthecolor.objects.ColorGenerator;
import com.zaidhuda.rememberthecolor.objects.SwipeInterface;

import java.util.Random;

public class PlayFragment extends Fragment implements SwipeInterface {
    private static final String ARG_DURATION = "gameDuration";
    private static final int TIME_REDUCTION = 2000;

    private long gameDuration;

    private View mContentView;
    private ProgressBar progressBar;
    private ObjectAnimator animation;
    private TextView scoreText;
    private RelativeLayout mainPanel, leftButton, rightButton;

    private Runnable endTask;
    private Handler endTaskHandler = new Handler();

    private boolean started, ended;
    private int currentMainColor;
    private int previousMainColor;
    private int score = -1;
    private int[] buttonColors = new int[2];

    private OnPlayFragmentInteractionListener mListener;

    public PlayFragment() {
    }

    public static PlayFragment newInstance(long gameDuration) {
        PlayFragment fragment = new PlayFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DURATION, gameDuration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameDuration = getArguments().getLong(ARG_DURATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.fragment_play, container, false);

        mainPanel = (RelativeLayout) mContentView.findViewById(R.id.main_color_panel);
        scoreText = (TextView) mContentView.findViewById(R.id.score_text);

        leftButton = (RelativeLayout) mContentView.findViewById(R.id.left_button);
        rightButton = (RelativeLayout) mContentView.findViewById(R.id.right_button);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(v);
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(v);
            }
        });

        mContentView.findViewById(R.id.progress_container).bringToFront();
        progressBar = (ProgressBar) mContentView.findViewById(R.id.progressBar);

        ActivitySwipeDetector swipe = new ActivitySwipeDetector(this);
        mainPanel.setOnTouchListener(swipe);

        startGame();

        return mContentView;
    }

    public void startGame() {
        ColorGenerator.setColorRand(new Random().nextInt(360));
        progressBar.setMax((int) gameDuration);
        progressBar.setProgress((int) gameDuration);

        changeBackground();
        previousMainColor = currentMainColor;
        changeButtonsBackground(previousMainColor);

        started = false;
        ended = false;
    }

    private void updateProgress() {
        endTaskHandler = new Handler();
        endTask = new Runnable() {
            @Override
            public void run() {
                end();
            }
        };
        endTaskHandler.postDelayed(endTask, gameDuration);

        animation = ObjectAnimator.ofInt(progressBar, "progress", 0);
        animation.setDuration(gameDuration);
        animation.setInterpolator(null);
        animation.start();
    }

    private void changeBackground() {
        generateMainPanelColor();
        mainPanel.getBackground().setColorFilter(currentMainColor, PorterDuff.Mode.SRC);

        ViewGroup container = (ViewGroup) mContentView.findViewById(R.id.container);
        if (ColorGenerator.isBrightColor(currentMainColor))
            ColorGenerator.toDark(container);
        else
            ColorGenerator.toBright(container);
    }

    private void generateMainPanelColor() {
        previousMainColor = currentMainColor;
        currentMainColor = ColorGenerator.generateColor();
    }

    public void changeButtonsBackground(int previousMainColor) {
        int randomColor = ColorGenerator.generateColor();
        Random rand = new Random();
        int random = rand.nextInt(2);

        if (random % 2 == 1) {
            changeButtonBackground(leftButton, previousMainColor);
            changeButtonBackground(rightButton, randomColor);
            buttonColors[0] = previousMainColor;
            buttonColors[1] = randomColor;
        } else {
            changeButtonBackground(leftButton, randomColor);
            changeButtonBackground(rightButton, previousMainColor);
            buttonColors[0] = randomColor;
            buttonColors[1] = previousMainColor;
        }
    }

    private void changeButtonBackground(final View v, final int color) {
        v.getBackground().setColorFilter(color, PorterDuff.Mode.SRC);
    }

    protected void selectRight() {
        onButtonPressed(rightButton);
    }

    protected void selectLeft() {
        onButtonPressed(leftButton);
    }

    protected void onButtonPressed(View view) {
        if (!started) {
            started = true;
            updateProgress();
        }
        if (!ended) {
            int color = 0;
            if (view.getId() == R.id.left_button)
                color = buttonColors[0];
            else if (view.getId() == R.id.right_button)
                color = buttonColors[1];

            if (!isPreviousColor(color)) {
//                end();
                long durationPlayed = animation.getCurrentPlayTime() + TIME_REDUCTION;
                animation.cancel();
                animation.start();
                animation.setCurrentPlayTime(durationPlayed);
                endTaskHandler.removeCallbacks(endTask);
                endTaskHandler.postDelayed(endTask, gameDuration - durationPlayed);
                return;
            }
            score++;
            scoreText.setText(String.valueOf(score));
            changeBackground();
            changeButtonsBackground(previousMainColor);
        }
    }

    protected boolean isPreviousColor(final int color) {
        return color == previousMainColor;
    }

    private void end() {
        ended = true;
        endTaskHandler.removeCallbacks(endTask);
        long played = animation.getCurrentPlayTime();
        animation.cancel();
        if (mListener != null) {
            mListener.onPlayFinished(score, gameDuration, played, previousMainColor);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            endTaskHandler.removeCallbacks(endTask);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            animation.cancel();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlayFragmentInteractionListener) {
            mListener = (OnPlayFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnPlayFragmentInteractionListener) {
            mListener = (OnPlayFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void bottom2top(View v) {
        end();
    }

    @Override
    public void left2right(View v) {
        selectRight();
    }

    @Override
    public void right2left(View v) {
        selectLeft();
    }

    @Override
    public void top2bottom(View v) {
        if (mListener != null)
            mListener.onQuit();
    }

    public interface OnPlayFragmentInteractionListener {
        void onPlayFinished(int score, long gameDuration, long duration, int lastColor);
        void onQuit();
    }
}
