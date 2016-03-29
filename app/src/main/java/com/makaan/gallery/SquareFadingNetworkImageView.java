package com.makaan.gallery;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.util.PhotoUtil;

public class SquareFadingNetworkImageView extends FadeInNetworkImageView {
	
	private Activity mActivity;

	public SquareFadingNetworkImageView(Context context) {
		this(context, null);
		
	}

	public SquareFadingNetworkImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		
	}

	public SquareFadingNetworkImageView(Context context, AttributeSet attrs,
										int defStyle) {
		super(context, attrs, defStyle);
		mActivity = (Activity) getContext();
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int width = PhotoUtil.getGridItemWidth(mActivity);		
		setMeasuredDimension(width, (int) (width* PhotoUtil.THUMBNAIL_ASPECT_RATIO));
	}

}
