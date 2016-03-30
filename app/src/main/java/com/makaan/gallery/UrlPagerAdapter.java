/*
 Copyright (c) 2013 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.makaan.gallery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.makaan.R;

import java.util.List;

/**
 * Class wraps URLs to adapter, then it instantiates {@link UrlTouchImageView}
 * objects to paging up through them.
 */
public class UrlPagerAdapter extends BasePagerAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;

	public UrlPagerAdapter(Activity context, List<String> resources) {
		super(context, resources);
		
		mContext = context;
		mInflater = LayoutInflater.from(mContext);

	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		
		ViewGroup vg = (ViewGroup) object;
		
		UrlTouchImageView iv = (UrlTouchImageView) vg.findViewById(R.id.url_touch_image_view);
		((GalleryViewPager) container).mCurrentView = iv.getImageView();
		
	}

	@Override
	public Object instantiateItem(ViewGroup collection, int position) {
		ViewGroup vg = (ViewGroup) mInflater.inflate(R.layout.full_screen_url_touchimage_with_placeholder, null);
		collection.addView(vg, 0);
		
		UrlTouchImageView iv = (UrlTouchImageView) vg.findViewById(R.id.url_touch_image_view);
		iv.setUrl(mResources.get(position));
		
		return vg;
	}

	@Override
	public void destroyItem(ViewGroup collection, int position, Object object) {
		super.destroyItem(collection, position, object);
	}
}
