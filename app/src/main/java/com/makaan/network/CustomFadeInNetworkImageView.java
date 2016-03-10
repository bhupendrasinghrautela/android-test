package com.makaan.network;

import android.content.Context;
import android.util.AttributeSet;

import com.android.volley.toolbox.FadeInNetworkImageView;

/**
 * Created by rohitgarg on 3/10/16.
 */
public class CustomFadeInNetworkImageView extends FadeInNetworkImageView {
    public CustomFadeInNetworkImageView(Context context) {
        super(context);
    }

    public CustomFadeInNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFadeInNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
