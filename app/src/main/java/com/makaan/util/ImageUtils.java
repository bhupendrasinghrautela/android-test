package com.makaan.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
	private static final String TAG = "ImageUtils";

	/**
	 * Stores an image on the storage
	 * 
	 * @param image
	 *            the image to store.
	 * @param pictureFile
	 *            the file in which it must be stored
	 */
	public static void storeImage(Bitmap image, File pictureFile) {
		if (pictureFile == null) {
			Log.d(TAG, "Error creating media file, check storage permissions: ");
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}

	/**
	 * Get the screen height.
	 * 
	 * @param context
	 * @return the screen height
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenHeight(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.y;
		}
		return display.getHeight();
	}

	/**
	 * Get the screen width.
	 * 
	 * @param context
	 * @return the screen width
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenWidth(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.x;
		}
		return display.getWidth();
	}

    /**
     *
     * @param url url of the image
     * @param width width of the image
     * @param height height of the image
     * @param dimensionsInDp if true, then width and height will be used as dp based dimension
     *                       so size will be used based on device density
     * @return url to be used
     */
	public static String getImageRequestUrl(String url, int width, int height, boolean dimensionsInDp) {
		if(TextUtils.isEmpty(url)){
			return url;
		}
        if(dimensionsInDp) {
            width = (int) (width * Resources.getSystem().getDisplayMetrics().density);
            height = (int) (height * Resources.getSystem().getDisplayMetrics().density);
        }
        return url.concat(String.format("?width=%d&height=%d", width, height));
    }

}
