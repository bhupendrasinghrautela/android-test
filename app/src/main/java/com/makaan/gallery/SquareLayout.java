package com.makaan.gallery;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.makaan.util.PhotoUtil;

public class SquareLayout extends FrameLayout {
	
	Activity mActivity;

	public SquareLayout(Context context) {
		this(context, null);
		
	}

	public SquareLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		
	}

	public SquareLayout(Context context, AttributeSet attrs,
						int defStyle) {
		super(context, attrs, defStyle);
		mActivity = (Activity) getContext();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int width = PhotoUtil.getGridItemWidth(mActivity);
		setMeasuredDimension(width, (int) (width*PhotoUtil.THUMBNAIL_ASPECT_RATIO));
	}

}
