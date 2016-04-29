package com.makaan.gallery;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.gallery.GalleryActivity.CategorizedImage;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.ui.FadeInNetworkImageView;
import com.makaan.util.CommonUtil;
import com.makaan.util.ImageUtils;

import java.util.List;

public class CategorizedGalleryThumbnailAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<CategorizedImage> mClubbedImageList;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	
	public CategorizedGalleryThumbnailAdapter(Context context, List<CategorizedImage> clubbedImageList) {
		
		mContext = context;
		mClubbedImageList = clubbedImageList;
		mInflater = LayoutInflater.from(mContext);
		mImageLoader = MakaanNetworkClient.getInstance().getImageLoader();
		
	}

	@Override
	public int getCount() {
		
		return mClubbedImageList.size();
	}

	@Override
	public CategorizedImage getItem(int position) {
	
		return mClubbedImageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Holder h = null;
		
		if(convertView == null) {
			h = new Holder();
			convertView = mInflater.inflate(R.layout.clubbed_image_gridview_item_layout, null);
			h.imageView = (FadeInNetworkImageView) convertView.findViewById(R.id.fading_network_image_view);
			h.categoryName = (TextView) convertView.findViewById(R.id.title);
			h.imageCount = (TextView) convertView.findViewById(R.id.count);
		
			convertView.setTag(h);
		} else {
			h = (Holder) convertView.getTag();
		}
		
		CategorizedImage data = getItem(position);
		String url = ImageUtils.getThumbnailUrl(data.overViewImage.getAbsolutePath());
		CommonUtil.TLog("Image Url: " + url);
        h.imageView.setImageUrl(url, mImageLoader);
		String title = mContext.getString(data.titleId);
		if(!TextUtils.isEmpty(title)) {
			h.categoryName.setText(title.toLowerCase());
		}
        h.imageCount.setText(String.valueOf(data.count) + " photos");
		return convertView;	
	}
	
	private class Holder {
		FadeInNetworkImageView imageView;
		TextView categoryName;
		TextView imageCount;
	}

}
