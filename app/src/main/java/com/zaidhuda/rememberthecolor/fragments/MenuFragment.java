package com.zaidhuda.rememberthecolor.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zaidhuda.rememberthecolor.R;
import com.zaidhuda.rememberthecolor.objects.ColorGenerator;
import com.zaidhuda.rememberthecolor.objects.PreferencesConst;

import java.util.Random;

public class MenuFragment extends Fragment {
    private final int MAX_DURATION = 60000;

    private View mContentView;
    private Button startButton, helpButton;
    private TextView durationTextView;
    private TextView bestScoreTextView;
    private SeekBar seekBar;
    private LinearLayout container;

    private long gameDuration = 0;
    private int bestScore;

    private OnMenuFragmentInteractionListener mListener;

    public MenuFragment() {
    }

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_menu, container, false);

        startButton = (Button) mContentView.findViewById(R.id.start_button);
        helpButton = (Button) mContentView.findViewById(R.id.noob_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPlay(gameDuration);
                }
            }
        });
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNoobSeekHelp();
                }
            }
        });

        durationTextView = (TextView) mContentView.findViewById(R.id.duration_text);
        durationTextView.setText(timeConversion(gameDuration));

        bestScoreTextView = (TextView) mContentView.findViewById(R.id.best_score_text);
        bestScore = PreferencesConst.getBestScore(getActivity(), gameDuration);
        bestScoreTextView.setText(String.valueOf(bestScore));

        seekBar = (SeekBar) mContentView.findViewById(R.id.duration_seekBar);
        seekBar.setProgress((int) gameDuration);
        seekBar.setMax(MAX_DURATION);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress /= 10000;
                progress *= 10000;
                gameDuration = progress;
                durationTextView.setText(timeConversion(gameDuration));
                bestScore = PreferencesConst.getBestScore(getActivity(), gameDuration);
                bestScoreTextView.setText(String.valueOf(bestScore));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ColorGenerator.setColorRand(new Random().nextInt(360));
        int background = ColorGenerator.generateColor();
        mContentView.setBackgroundColor(background);

        this.container = (LinearLayout) mContentView.findViewById(R.id.container);

        if (ColorGenerator.isBrightColor(background))
            toDark(this.container);
        else
            toBright(this.container);

        return mContentView;
    }

    private String timeConversion(long totalSeconds) {
        totalSeconds = totalSeconds/1000;
        if (totalSeconds > 0)
            return totalSeconds + getResources().getString(R.string.seconds_text);
        return getResources().getString(R.string.casual_text);
    }

    private void toDark(ViewGroup ll) {
        ColorGenerator.toDark(ll);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_play_arrow_black_48dp, null), null, null, null);
            helpButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_help_black_48dp, null), null, null, null);
        } else {
            startButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_play_arrow_black_48dp), null, null, null);
            helpButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_help_black_48dp), null, null, null);
        }
    }

    private void toBright(ViewGroup ll) {
        ColorGenerator.toBright(ll);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_play_arrow_white_48dp, null), null, null, null);
            helpButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_help_white_48dp, null), null, null, null);
        } else {
            startButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_play_arrow_white_48dp), null, null, null);
            helpButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_help_white_48dp), null, null, null);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMenuFragmentInteractionListener) {
            mListener = (OnMenuFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnMenuFragmentInteractionListener) {
            mListener = (OnMenuFragmentInteractionListener) activity;
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

    public interface OnMenuFragmentInteractionListener {
        void onPlay(long duration);
        void onNoobSeekHelp();
    }
}
