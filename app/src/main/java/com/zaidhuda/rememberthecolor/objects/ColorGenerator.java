package com.zaidhuda.rememberthecolor.objects;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Zaid on 29/12/2015.
 */
public final class ColorGenerator {
    private static int COLOR_RAND;

    public static void setColorRand(int colorRand) {
        COLOR_RAND = colorRand;
    }

    public static int generateColor() {
        COLOR_RAND = (COLOR_RAND + 50 + new Random().nextInt(50)) % 360;
        float h = COLOR_RAND;
        float hsl[] = {h, 1f, 0.65f};
        return ColorUtils.HSLToColor(hsl);
    }

    public static boolean isBrightColor(int color) {
        if (android.R.color.transparent == color)
            return true;

        boolean rtnValue = false;

        int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };

        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);

        // color is light
        if (brightness >= 200) {
            rtnValue = true;
        }

        return rtnValue;
    }

    public static void toDark(ViewGroup layout) {
        int black = Color.BLACK;

        int children = layout.getChildCount();
        for (int i=0; i < children; i++){
            View v = layout.getChildAt(i);
            if (v instanceof ViewGroup)
                toDark((ViewGroup) v);
            if (v instanceof TextView)
                ((TextView) v).setTextColor(black);
        }
    }

    public static void toBright(ViewGroup layout) {
        int white = Color.WHITE;

        int children = layout.getChildCount();
        for (int i=0; i < children; i++){
            View v = layout.getChildAt(i);
            if (v instanceof ViewGroup)
                toBright((ViewGroup) v);
            if (v instanceof TextView)
                ((TextView) v).setTextColor(white);
        }
    }
}
