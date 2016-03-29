package com.makaan.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.makaan.R;

public class PhotoUtil {

    private static DisplayMetrics mDisplayMetrics = null;
    private static final String THUMBNAIL_IMAGE_DIMEN = "?WIDTH=220&HEIGHT=120";
    public static final float THUMBNAIL_ASPECT_RATIO = 8f/10f;

    public static String getScaledImageUrl(Activity activity, String imageUrl) {
        try {
            String imageDimen = "";
            // Determine screen size
            if (mDisplayMetrics == null) {
                Display display = activity.getWindowManager().getDefaultDisplay();
                mDisplayMetrics = new DisplayMetrics();
                display.getMetrics(mDisplayMetrics);
            }

            float dpHeight = mDisplayMetrics.heightPixels;

            if (dpHeight > 300 && dpHeight <= 480) {
                imageDimen = "?WIDTH=220&HEIGHT=120";
            } else if (dpHeight > 480 && dpHeight <= 720) {
                imageDimen = "?WIDTH=280&HEIGHT=200";
            } else if (dpHeight > 720 && dpHeight <= 940) {
                imageDimen = "?WIDTH=320&HEIGHT=220";
            } else if (dpHeight > 940 && dpHeight <= 1050) {
                imageDimen = "?WIDTH=360&HEIGHT=240";
            } else if (dpHeight > 1050 && dpHeight <= 1200) {
                imageDimen = "?WIDTH=420&HEIGHT=280";
            } else if (dpHeight > 1200 && dpHeight <= 1400) {
                imageDimen = "?WIDTH=480&HEIGHT=320";
            } else if (dpHeight > 1400) {
                imageDimen = "?WIDTH=520&HEIGHT=340";
            }

            return imageUrl + imageDimen;

        } catch (Exception e) {
            return imageUrl;
        }

    }

    public static String getFullScreenImageUrl(Activity activity, String imageUrl) {
        String imageDimen = "";
        try {
            // Determine screen size
            if (mDisplayMetrics == null) {
                Display display = activity.getWindowManager().getDefaultDisplay();
                mDisplayMetrics = new DisplayMetrics();
                display.getMetrics(mDisplayMetrics);
            }
            float dpHeight = mDisplayMetrics.heightPixels;

            if (dpHeight > 300 && dpHeight <= 480) {
                imageDimen = "?WIDTH=320&HEIGHT=240";
            } else if (dpHeight > 480 && dpHeight <= 720) {
                imageDimen = "?WIDTH=380&HEIGHT=280";
            } else if (dpHeight > 720 && dpHeight <= 940) {
                imageDimen = "?WIDTH=520&HEIGHT=400";
            } else if (dpHeight > 940 && dpHeight <= 1050) {
                imageDimen = "?WIDTH=680&HEIGHT=580";
            } else if (dpHeight > 1050 && dpHeight <= 1200) {
                imageDimen = "?WIDTH=800&HEIGHT=620";
            } else if (dpHeight > 1200 && dpHeight <= 1400) {
                imageDimen = "?WIDTH=940&HEIGHT=720";
            } else if (dpHeight > 1400) {
                imageDimen = "?WIDTH=1040&HEIGHT=780";
            }
            return imageUrl + imageDimen ;
        } catch (Exception e) {
            return imageUrl;
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
