package com.zaidhuda.rememberthecolor.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zaidhuda.rememberthecolor.R;
import com.zaidhuda.rememberthecolor.fragments.GameOverFragment;
import com.zaidhuda.rememberthecolor.fragments.MenuFragment;
import com.zaidhuda.rememberthecolor.fragments.PlayEndlessFragment;
import com.zaidhuda.rememberthecolor.fragments.PlayFragment;

public class MainActivity extends AppCompatActivity implements
        MenuFragment.OnMenuFragmentInteractionListener,
        PlayFragment.OnPlayFragmentInteractionListener,
        PlayEndlessFragment.OnPlayEndlessFragmentInteractionListener,
        GameOverFragment.OnGameOverFragmentInteractionListener {
    protected static final String NAME_MENU = "menu";
    protected static final String NAME_PLAY = "play";
    protected static final String NAME_PLAYENDLESS = "play_endless";
    protected static final String NAME_GAMEOVER = "game_over";

    private View mContentView;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fragmentManager = getFragmentManager();

        setContentView(R.layout.activity_main);

        mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        displayMenuFragment();
    }

    private void displayMenuFragment() {
        displayFragment(MenuFragment.newInstance(), NAME_MENU);
    }

    private void displayPlayFragment(long gameDuration) {
        displayFragment(PlayFragment.newInstance(gameDuration), NAME_PLAY);
    }

    private void displayPlayEndlessFragment() {
        displayFragment(PlayEndlessFragment.newInstance(), NAME_PLAYENDLESS);
    }

    private void displayGameOverFragment(int score, long gameDuration, long duration, int lastColor) {
        displayFragment(GameOverFragment.newInstance(score, gameDuration, duration, lastColor), NAME_GAMEOVER);
    }

    private void displayFragment(Fragment fragment, String name) {
        fragmentManager.beginTransaction()
                .replace(R.id.fullscreen_content, fragment, name)
                .addToBackStack(name)
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1);
        String name = backEntry.getName();
        Fragment fragment = fragmentManager.findFragmentByTag(name);

        if (fragment instanceof MenuFragment) {
            super.onBackPressed();
        } else {
            do {
                fragmentManager.popBackStackImmediate();
                backEntry = getFragmentManager().getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1);
                name = backEntry.getName();
                fragment = fragmentManager.findFragmentByTag(name);
                System.out.println(name);
            } while (!(fragment instanceof MenuFragment));
            displayFragment(fragment, name);
        }
    }

    @Override
    public void onPlay(long gameDuration) {
        if (gameDuration > 0)
            displayPlayFragment(gameDuration);
        else
            displayPlayEndlessFragment();
    }

    @Override
    public void onNoobSeekHelp() {
        new AlertDialog.Builder(this)
                .setTitle("Seriously?")
                .setMessage(R.string.noob_confirmation)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onNoobSeekHelp();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onPlayFinished(int score, long gameDuration, long duration, int lastColor) {
        displayGameOverFragment(score, gameDuration, duration, lastColor);
    }

    @Override
    public void onPlayFinished(int score, long durationPlayed, int lastColor) {
        displayGameOverFragment(score, 0, durationPlayed, lastColor);
    }

    @Override
    public void onQuit() {
        onBackPressed();
    }

    @Override
    public void onRetry(long gameDuration) {
        if (gameDuration > 0)
            displayPlayFragment(gameDuration);
        else
            displayPlayEndlessFragment();
    }
}
