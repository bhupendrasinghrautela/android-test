package com.makaan.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.makaan.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
	private static final String TAG = "ImageUtils";
	private static final String THUMBNAIL_IMAGE_DIMEN = "?WIDTH=220&HEIGHT=120";
	public static final float THUMBNAIL_ASPECT_RATIO = 8f/10f;

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
        //return url.concat(String.format("?width=%d&height=%d", width, height));
		return getScaledImageUrl(url);
    }

	public static String getScaledImageUrl(String imageUrl) {
		//TODO host this in config file

		if(TextUtils.isEmpty(imageUrl)){
			//TODO show some default imageurl
			return imageUrl;
		}

		try {
			String imageDimen = "";

			switch (Resources.getSystem().getDisplayMetrics().densityDpi) {
				case DisplayMetrics.DENSITY_LOW:
					imageDimen = "?width=220&height=120";
					break;
				case DisplayMetrics.DENSITY_MEDIUM:
					imageDimen = "?width=280&height=200";
					break;
				case DisplayMetrics.DENSITY_HIGH:
				case DisplayMetrics.DENSITY_280:
					imageDimen = "?width=320&height=220";
					break;
				case DisplayMetrics.DENSITY_XHIGH:
				case DisplayMetrics.DENSITY_360:
					imageDimen = "?width=360&height=240";
					break;
				case DisplayMetrics.DENSITY_XXHIGH:
				case DisplayMetrics.DENSITY_400:
				case DisplayMetrics.DENSITY_420:
					imageDimen = "?width=420&height=280";
					break;
				case DisplayMetrics.DENSITY_XXXHIGH:
				case DisplayMetrics.DENSITY_560:
					imageDimen = "?width=520&height=340";
					break;
				default:
					imageDimen = "?width=280&height=200";
					break;
			}

			//TODO this is a temp code, remove after redirection issue is fixed
			if(imageUrl.contains("http") && !imageUrl.contains("https")){
				imageUrl = imageUrl.replace("http","https");
			}

			return imageUrl.concat(imageDimen);

		} catch (Exception e) {
			return imageUrl.concat("?width=380&height=280");
		}
	}

	public static String getFullScreenImageUrl(String imageUrl) {
		//TODO host this in config file

		if(TextUtils.isEmpty(imageUrl)){
			//TODO show some default imageurl
			return imageUrl;
		}

		try {
			String imageDimen = "";

			switch (Resources.getSystem().getDisplayMetrics().densityDpi) {
				case DisplayMetrics.DENSITY_LOW:
					imageDimen = "?WIDTH=320&HEIGHT=240";
					break;
				case DisplayMetrics.DENSITY_MEDIUM:
					imageDimen = "?WIDTH=380&HEIGHT=280";
					break;
				case DisplayMetrics.DENSITY_HIGH:
				case DisplayMetrics.DENSITY_280:
					imageDimen = "?WIDTH=520&HEIGHT=400";
					break;
				case DisplayMetrics.DENSITY_XHIGH:
				case DisplayMetrics.DENSITY_360:
					imageDimen = "?WIDTH=680&HEIGHT=580";
					break;
				case DisplayMetrics.DENSITY_XXHIGH:
				case DisplayMetrics.DENSITY_400:
				case DisplayMetrics.DENSITY_420:
					imageDimen = "?WIDTH=800&HEIGHT=620";
					break;
				case DisplayMetrics.DENSITY_XXXHIGH:
				case DisplayMetrics.DENSITY_560:
					imageDimen = "?WIDTH=940&HEIGHT=720";
					break;
				default:
					imageDimen = "?WIDTH=380&HEIGHT=280";
					break;
			}

			//TODO this is a temp code, remove after redirection issue is fixed
			if(imageUrl.contains("http") && !imageUrl.contains("https")){
				imageUrl = imageUrl.replace("http","https");
			}

			return imageUrl.concat(imageDimen);

		} catch (Exception e) {
			return imageUrl.concat("?WIDTH=380&HEIGHT=280");
		}
	}
	public static String getThumbnailUrl(String imageUrl) {
		try {
			return imageUrl+ THUMBNAIL_IMAGE_DIMEN;
		} catch (Exception e) {
			return imageUrl;
		}
	}

	public static int getNumColumns(Context context) {

		int numColumns = 2;
		int orientation = getOrientation(context);

		if (orientation == Surface.ROTATION_90
				|| orientation == Surface.ROTATION_270) {
			// landscape mode
			numColumns = context.getResources().getInteger(R.integer.landscape_mode_num_columns);
		} else {
			// portrait mode
			numColumns = context.getResources().getInteger(R.integer.portrait_mode_num_columns);
		}

		return numColumns;
	}

	public static int getOrientation(Context context) {

		Activity activity = (Activity) context;

		Display display = ((WindowManager) activity.getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay();
		int orientation = display.getRotation();

		return orientation;
	}

	public static int getGridItemWidth(Activity activity) {

		int width = CommonUtil.getScreenWidthInPixels(activity);
		int numOfColumns = getNumColumns(activity);
		width = width / numOfColumns;

		return width;
	}
}
