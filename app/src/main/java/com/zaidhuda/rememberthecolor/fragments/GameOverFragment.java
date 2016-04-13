package com.zaidhuda.rememberthecolor.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zaidhuda.rememberthecolor.R;
import com.zaidhuda.rememberthecolor.objects.BitmapShare;
import com.zaidhuda.rememberthecolor.objects.ColorGenerator;
import com.zaidhuda.rememberthecolor.objects.PreferencesConst;

import java.util.Timer;
import java.util.TimerTask;

public class GameOverFragment extends Fragment {
    private static final String ARG_DURATION = "playedDuration";
    private static final String ARG_GAME_DURATION = "gameDuration";
    private static final String ARG_SCORE = "score";
    private static final String ARG_BACKGROUND = "background";

    private View mContentView;
    private TextView bestScoreTextView, recentScoreTextView, durationText;
    private Button retryButton, shareButton;
    private LinearLayout container, buttonGroups;

    private int score;
    private long gameDuration, playedDuration;
    private int background = Color.parseColor("#ff2f2f2f");

    private OnGameOverFragmentInteractionListener mListener;

    public GameOverFragment() {
    }

    public static GameOverFragment newInstance(int score, long gameDuration, long playedDuration) {
        GameOverFragment fragment = new GameOverFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DURATION, playedDuration);
        args.putLong(ARG_GAME_DURATION, gameDuration);
        args.putInt(ARG_SCORE, score);
        fragment.setArguments(args);
        return fragment;
    }

    public static GameOverFragment newInstance(int score, long gameDuration, long playedDuration, int background) {
        GameOverFragment fragment = new GameOverFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DURATION, playedDuration);
        args.putLong(ARG_GAME_DURATION, gameDuration);
        args.putInt(ARG_SCORE, score);
        args.putInt(ARG_BACKGROUND, background);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playedDuration = getArguments().getLong(ARG_DURATION) + 100;
            gameDuration = getArguments().getLong(ARG_GAME_DURATION);
            score = getArguments().getInt(ARG_SCORE);
            if (getArguments().getInt(ARG_BACKGROUND) != 0)
                background = getArguments().getInt(ARG_BACKGROUND);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_game_over, container, false);
        mContentView.setBackgroundColor(background);

        bestScoreTextView = (TextView) mContentView.findViewById(R.id.best_score_text);
        recentScoreTextView = (TextView) mContentView.findViewById(R.id.recent_score_text);
        durationText = (TextView) mContentView.findViewById(R.id.duration_text);

        recentScoreTextView.setText(String.valueOf(score));

        String durationTextContent;
        if (gameDuration > 0)
            durationTextContent = gameDuration/1000 + getResources().getString(R.string.seconds_text);
        else
            durationTextContent = getResources().getString(R.string.casual_text).toUpperCase();

        durationText.setText(durationTextContent);

        if (score < 1) {
            recentScoreTextView.setText(R.string.too_noob_text);
            score = 0;
        }
        else if (playedDuration < gameDuration) {
            recentScoreTextView.setText(String.valueOf(score));
            recentScoreTextView.setTextColor(Color.GRAY);
            score = 0;
        }

        getBestScore();

        shareButton = (Button) mContentView.findViewById(R.id.share_button);
        retryButton = (Button) mContentView.findViewById(R.id.retry_button);
        buttonGroups = (LinearLayout) mContentView.findViewById(R.id.button_groups);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonGroups.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    public void run() {
                        BitmapShare.saveImage(getActivity(), BitmapShare.getBitmapFromView(mContentView));
                        BitmapShare.share(getActivity());
                        buttonGroups.post(new Runnable() {
                            public void run() {
                                buttonGroups.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).start();
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRetry(gameDuration);
                }
            }
        });

        this.container = (LinearLayout) mContentView.findViewById(R.id.container);

        if (ColorGenerator.isBrightColor(background))
            toDark(this.container);
        else
            toBright(this.container);

        return mContentView;
    }

    private void getBestScore() {
        int bestScore = PreferencesConst.getBestScore(getActivity(), gameDuration);
        if (bestScore <= score) {
            PreferencesConst.setBestScore(getActivity(), gameDuration, score);
        }
        bestScore = PreferencesConst.getBestScore(getActivity(), gameDuration);
        bestScoreTextView.setText(String.valueOf(bestScore));
    }

    private void toDark(LinearLayout ll) {
        ColorGenerator.toDark(ll);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            retryButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_replay_black_48dp, null), null, null, null);
            shareButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_share_black_48dp, null), null, null, null);
        } else {
            retryButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_replay_black_48dp), null, null, null);
            shareButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_share_black_48dp), null, null, null);
        }
    }

    private void toBright(LinearLayout ll) {
        ColorGenerator.toBright(ll);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            retryButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_replay_white_48dp, null), null, null, null);
            shareButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_share_white_48dp, null), null, null, null);
        } else {
            retryButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_replay_white_48dp), null, null, null);
            shareButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_share_white_48dp), null, null, null);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGameOverFragmentInteractionListener) {
            mListener = (OnGameOverFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnGameOverFragmentInteractionListener) {
            mListener = (OnGameOverFragmentInteractionListener) activity;
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

    public interface OnGameOverFragmentInteractionListener {
        void onRetry(long gameDuration);
    }
}
