package com.zaidhuda.rememberthecolor.objects;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Zaid on 29/12/2015.
 */
public final class BitmapShare {
    public static void share(Context ctx) {
        File imagePath = new File(ctx.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(ctx, "com.zaidhuda.rememberthecolor.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri, ctx.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            ctx.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

    public static void saveImage(Context ctx, Bitmap finalBitmap) {
        try {
            File cachePath = new File(ctx.getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        return getBitmapFromView(view, Color.parseColor("#ff2f2f2f"));
    }

    public static Bitmap getBitmapFromView(View view, String color) {
        return getBitmapFromView(view, Color.parseColor(color));
    }

    public static Bitmap getBitmapFromView(View view, int defaultBackground) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(defaultBackground);
        view.draw(canvas);
        return returnedBitmap;
    }
}
