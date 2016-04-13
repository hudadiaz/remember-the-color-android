package com.zaidhuda.rememberthecolor.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zaidhuda.rememberthecolor.R;
import com.zaidhuda.rememberthecolor.objects.ActivitySwipeDetector;
import com.zaidhuda.rememberthecolor.objects.ColorGenerator;
import com.zaidhuda.rememberthecolor.objects.SwipeInterface;

import java.util.Date;
import java.util.Random;

public class PlayEndlessFragment extends Fragment implements SwipeInterface {

    protected View mContentView;
    protected TextView scoreText;
    protected RelativeLayout mainPanel, leftButton, rightButton;

    protected boolean started, ended;
    protected long startTime, endTime;
    protected int currentMainColor;
    protected int previousMainColor;
    protected int score = -1;
    protected int[] buttonColors = new int[2];

    private OnPlayEndlessFragmentInteractionListener mListener;

    public PlayEndlessFragment() {
    }

    public static PlayEndlessFragment newInstance() {
        PlayEndlessFragment fragment = new PlayEndlessFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.fragment_play, container, false);

        mContentView.findViewById(R.id.progress_container).setVisibility(View.GONE);
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

        ActivitySwipeDetector swipe = new ActivitySwipeDetector(this);
        mainPanel.setOnTouchListener(swipe);

        startGame();

        return mContentView;
    }

    protected void startGame() {
        ColorGenerator.setColorRand(new Random().nextInt(360));

        changeBackground();
        previousMainColor = currentMainColor;
        changeButtonsBackground(previousMainColor);

        started = false;
        ended = false;
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

    protected void changeButtonsBackground(int previousMainColor) {
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

    protected void changeButtonBackground(final View v, final int color) {
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
            startTime = new Date().getTime();
        }
        if (!ended) {
            int color = 0;
            if (view.getId() == R.id.left_button)
                color = buttonColors[0];
            else if (view.getId() == R.id.right_button)
                color = buttonColors[1];

            if (!isPreviousColor(color)) {
                end();
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

    protected void end() {
        ended = true;
        endTime = new Date().getTime();
        long durationPlayed = endTime - startTime;
        if (mListener != null) {
            mListener.onPlayFinished(score, durationPlayed, previousMainColor);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlayEndlessFragmentInteractionListener) {
            mListener = (OnPlayEndlessFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnPlayEndlessFragmentInteractionListener) {
            mListener = (OnPlayEndlessFragmentInteractionListener) activity;
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

    public interface OnPlayEndlessFragmentInteractionListener {
        void onPlayFinished(int score, long durationPlayed, int lastColor);
        void onQuit();
    }
}
