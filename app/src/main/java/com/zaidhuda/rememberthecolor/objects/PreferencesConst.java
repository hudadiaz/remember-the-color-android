package com.zaidhuda.rememberthecolor.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Zaid on 29/12/2015.
 */
public final class PreferencesConst {
    public static int getBestScore(Context ctx, long gameDuration) {
        String name = "score_" + String.valueOf(gameDuration);
        return PreferenceManager.getDefaultSharedPreferences(ctx).getInt(name, 0);
    }

    public static void setBestScore(Context ctx, long gameDuration, int score) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        editor.putInt("score_" + String.valueOf(gameDuration), score);
        editor.apply();
    }
}
