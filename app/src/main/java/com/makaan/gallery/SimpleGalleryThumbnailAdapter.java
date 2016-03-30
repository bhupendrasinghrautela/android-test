package com.makaan.gallery;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.image.Image;
import com.makaan.util.CommonUtil;
import com.makaan.util.ImageUtils;

import java.util.List;

public class SimpleGalleryThumbnailAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Image> mImageList;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private Activity mActivity;
	private int mFromPosition = 0;
	private long mAnimDuration = 500;
	private int mScreenHeight, mScreenWidth;
	private GridView mGridView;
	private int mVisibleCellCounts = 0;
	
	public SimpleGalleryThumbnailAdapter(Context context, List<Image> imageList) {
		mContext = context;
		mImageList = imageList;
		mInflater = LayoutInflater.from(mContext);
		mImageLoader = MakaanNetworkClient.getInstance().getImageLoader();
		mActivity = (Activity) context;
		
		mScreenHeight = CommonUtil.getScreenHeightInPixels(mActivity);
		mScreenWidth = CommonUtil.getScreenWidthInPixels(mActivity);
	}
	
	public void setAnimationProperties(GridView gridView, int fromPosition, long animDuration) {
		mGridView = gridView;
		mFromPosition = fromPosition;	
		mAnimDuration =  animDuration;
		mVisibleCellCounts = getVisibleCellCount();
	}

	@Override
	public int getCount() {
		
		return mImageList.size();
	}
	
	public void setData(List<Image> data) {
		mImageList = data;
		notifyDataSetChanged();
	}
	
	public List<Image> getData() {
		return mImageList;
	}

	@Override
	public Image getItem(int position) {
		
		return mImageList.get(position);
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
			convertView = mInflater.inflate(R.layout.gallery_image_gridview_item_layout, null);
			h.imageView = (FadeInNetworkImageView) convertView.findViewById(R.id.fading_network_image_view);
			h.title =(TextView) convertView.findViewById(R.id.grid_item_textview);
			convertView.setTag(h);
								
			animateFromClickedCell(convertView, position);
			
		} else {
			h = (Holder) convertView.getTag();
		}
		
		Image data = getItem(position);
		String url = ImageUtils.getThumbnailUrl(data.getAbsolutePath());
        h.imageView.setImageUrl(url, mImageLoader);
        
        String title = data.getTitle();
        if(!TextUtils.isEmpty(title)) {
        	h.title.setText(title);
        	h.title.setVisibility(View.VISIBLE);
        } else {
        	h.title.setText("");
        	h.title.setVisibility(View.INVISIBLE);
        	
        }
        
		return convertView;
	}
	
	private class Holder {
		
		FadeInNetworkImageView imageView;
		TextView title;
	}
	
	private void animateFromClickedCell(View view, int position) {
		
		if(!CommonUtil.isIcsAndAbove()) return;
		
		if(position > mVisibleCellCounts -1) {
			return;
		}
		
		int currentX = getX(position);
		int currentY = getY(position);
		int offSetX = getX(mFromPosition);
		int offSetY = getY( mFromPosition);
		
		int translateXBy = offSetX - currentX;
		int translateYBy = offSetY - currentY;
		
		view.setTranslationX(translateXBy);
		view.setTranslationY(translateYBy);
		
		view.animate()
			.alphaBy(0.6f)
			.translationX(0)
			.translationY(0)
			.setInterpolator(new DecelerateInterpolator())
			.setDuration(mAnimDuration);
	}
	
	public void animateBackToClickedCell() {
		
		if(!CommonUtil.isIcsAndAbove()) return;
		
		int firstVisiblePosition = mGridView.getFirstVisiblePosition();
		int lastVisiblePosition = mGridView.getLastVisiblePosition();
		int visibleItemCount = lastVisiblePosition - firstVisiblePosition + 1;
		
		for(int position = 0; position < visibleItemCount; position++) {
			View view = mGridView.getChildAt(position);
			
			if(view == null) {
				continue;
			}
			
			int currentX = getX(position);
			int currentY = getY(position);
			int offSetX = getX(mFromPosition);
			int offSetY = getY( mFromPosition);
			
			int translateXBy = offSetX - currentX;
			int translateYBy = offSetY - currentY;
			
			view.
				animate()
				.translationXBy(translateXBy)
				.translationYBy(translateYBy)
				.setInterpolator(new AccelerateInterpolator())
				.setDuration(mAnimDuration);
		}
		
	}
	
	private int getX(int position) {
		
		int numColumns = ImageUtils.getNumColumns(mContext);
    	int p = position % numColumns;    	
    	int cellWidth = mScreenWidth / numColumns;    	
    	
    	return p*cellWidth;
    }
    
    private int getY(int position) {
    	
    	int numColumns = ImageUtils.getNumColumns(mContext);
    	int p = position / numColumns;
    	int cellWidth = mScreenWidth / numColumns;   
    	int cellHeight = (int) (cellWidth*ImageUtils.THUMBNAIL_ASPECT_RATIO);
    	
    	int h = p*cellHeight;
    	
    	while (h > mScreenHeight) {
			h = h - mScreenHeight;
		}

    	return h;
    }
    
	private int getVisibleCellCount() {
		int numColumns = ImageUtils.getNumColumns(mContext);
		int cellWidth = mScreenWidth / numColumns;
		int cellHeight = (int) (cellWidth*ImageUtils.THUMBNAIL_ASPECT_RATIO);
		
		int gridViewHieght = mGridView.getHeight();
		
		int cell = 0;
		int h = 0;
		while( h < gridViewHieght) {
			h += cellHeight;
			cell += numColumns;
		}
		return cell;
	}

}
