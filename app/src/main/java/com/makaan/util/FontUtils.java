package com.makaan.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by rohitgarg on 1/29/16.
 */
public class FontUtils {

    public static Typeface getRegularTypeFace(Context mContext)
    {
        Typeface typeFaceRegular = Typeface.createFromAsset(mContext.getAssets(), "fonts/ProximaNova-Regular.otf");
        return typeFaceRegular;
    }

    public static Typeface getBoldTypeFace(Context mContext)
    {
        Typeface typeFaceRegular = Typeface.createFromAsset(mContext.getAssets(), "fonts/ProximaNova-Bold.otf");
        return typeFaceRegular;
    }

    public static Typeface getSemiBoldTypeFace(Context mContext)
    {
        Typeface typeFaceRegular = Typeface.createFromAsset(mContext.getAssets(), "fonts/ProximaNova-Semibold.otf");
        return typeFaceRegular;
    }

    public static Typeface getLightTypeFace(Context mContext)
    {
        Typeface typeFaceRegular = Typeface.createFromAsset(mContext.getAssets(), "fonts/ProximaNova-Light.otf");
        return typeFaceRegular;
    }



}
