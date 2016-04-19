package com.makaan.gallery;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.crashlytics.android.Crashlytics;
import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.gallery.BasePagerAdapter.OnItemChangeListener;
import com.makaan.response.image.Image;
import com.makaan.util.CommonUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends MakaanFragmentActivity {
	
	public static final String SCREEN_NAME = "Gallery";

	private GridView mSimpleGridView, mCategorizedGridView;
	private GalleryViewPager mViewPager;
	private View mGridViewContainer;
	
	private CategorizedGalleryThumbnailAdapter mCategorizedGalleryThumbnailAdapter;
	
	private GalleryType mGalleryType = GalleryType.SimpleGridView;
	private List<Image> mProjectAllImagesList = new ArrayList<Image>();
	private List<CategorizedImage> mCategorizedImageOverViewList = new ArrayList<CategorizedImage>();
	
	private List<Image> mProjectImagesMainPhotoList = new ArrayList<Image>();
	private List<Image> mProjectImagesProjectPlansList = new ArrayList<Image>();
	private List<Image> mProjectImagesPaymentPlanList = new ArrayList<Image>();
	private List<Image> mProjectImagesConstructionStatusList = new ArrayList<Image>();
	private List<Image> mProjectImagesFloorPlanList = new ArrayList<Image>();
	private List<Image> mProjectImageGalleryList = new ArrayList<Image>();
	private List<Image> mProjectNeighbourhoodImageList = new ArrayList<Image>();
	private List<Image> mFullScrennImageList = new ArrayList<Image>();
	
	private UrlPagerAdapter mUrlPagerAdapter;
	private PageChangeListener mPageChangeListener;

	private Context mContext;
	private boolean mCategorizedGridViewSkipped = false;
	private boolean mSimpleGridViewSkipped = false;
	private boolean mDirectlyLaunchFullScreen = false;
	
	private String sScreenName;

	private CharSequence mPrevTitle, mPrevSubtitle;
	private long mAnimationDuration = 500;
	private String mCurrentCategoryName = "";
	private ActionBar mActionBar;
	
	private int mSimpleGridScrollPosition;
	
	private enum GalleryType {
		SimpleGridView(0), CategorizedGridView(1), FullScreenImageView(2);
		
		private GalleryType(int value) {
			
		}
	}

	@Override
	protected int getContentViewId() {
		return R.layout.image_gallery_layout;
	}

	@Override
	public boolean isJarvisSupported() {
		return false;
	}

	@Override
	public String getScreenName() {
		return SCREEN_NAME;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	

		if(getIntent()==null || getIntent().getExtras() == null || !getIntent().hasExtra(KeyUtil.IMAGE_DATA_LIST)) {
			finish();
			return;
		}
		Bundle mData = getIntent().getExtras();
		mContext = this;
		List<Image> images = mData.getParcelableArrayList(KeyUtil.IMAGE_DATA_LIST);
		mProjectNeighbourhoodImageList = mData.getParcelableArrayList(KeyUtil.NEIGHBOURHOODS);
		sScreenName = mData.getString(KeyUtil.KEY_FULL_SCREEN_SRC_NAME);
		if(sScreenName == null) {
			sScreenName = SCREEN_NAME;
		}
		mDirectlyLaunchFullScreen = mData.getBoolean(KeyUtil.KEY_LAUNCH_FULL_SCREEN_GALLERY_DIRECTLY, false);
		
		if(images == null) {
			images = new ArrayList<Image>();
		}
		
		if(mProjectNeighbourhoodImageList == null) {
			mProjectNeighbourhoodImageList = new ArrayList<Image>();
		}
		setupActionBar();
		setupGridView();
		setupGalleryViewPager();
		
		if(mDirectlyLaunchFullScreen) { // right now only from locality photos fragment
			int position = mData.getInt(KeyUtil.KEY_PROJECT_IMAGES_CLICKED_POSITION, 0);
			mFullScrennImageList = images;
			mCurrentCategoryName = "Locality Photos";
			openFullScreenImageView(position);
		} else {			
			setProjectImages(images);			
			openCategorizedGridView();
		}
	}
	
	private void setupActionBar() {

		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
		if (VERSION.SDK_INT >= VERSION_CODES.M) {
			mToolbar.setTitleTextColor(getResources().getColor(R.color.white,null));
			mToolbar.setSubtitleTextColor(getResources().getColor(R.color.white,null));
		}
		else {
			mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
			mToolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
		}
		setSupportActionBar(mToolbar);
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeAsUpIndicator(R.mipmap.back_white);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);
	}

	private void setupGridView() {
		
		mGridViewContainer = findViewById(R.id.grid_container);
		
		int numOfColumns = ImageUtils.getNumColumns(mContext);
		
		setupSimpleGridView(numOfColumns);
		setupCategorizedGridView(numOfColumns);	
	}

	private void setupSimpleGridView(int numOfColumns) {
		
		mSimpleGridView = (GridView)  findViewById(R.id.simple_grid_view);
		mSimpleGridView.setNumColumns(numOfColumns);
		mSimpleGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				
				openFullScreenImageView(position);
			}
		});		
		
		mSimpleGridView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				
				mSimpleGridScrollPosition = firstVisibleItem;
			}
		});
	}
	
	private void setupCategorizedGridView(int numOfColumns) {
		mCategorizedGridView = (GridView) findViewById(R.id.categorized_grid_view);
		mCategorizedGridView.setNumColumns(numOfColumns);
		mCategorizedGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				
				openSimpleGridView(position);
			}
		});
	}
	
	private void setupGalleryViewPager() {
		mViewPager = (GalleryViewPager) findViewById(R.id.project_details_page_fullscreen_gallery_view);
        //mViewPager.setPageTransformer(true, new DepthPageTransformer());
	}
	
	private List<String> getFullScreenUrlList(List<Image> ImageList) {
		
		List<String> urls = new ArrayList<String>();
		for (Image image : ImageList) {
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(image.getAbsolutePath());
			urls.add(ImageUtils.getFullScreenImageUrl(image.getAbsolutePath()));
			CommonUtil.TLog("Image Full Screen Urls: " + urlBuilder.toString());
		}
		
		return urls;
	}
	
	private class PageChangeListener implements OnItemChangeListener {
		
		private List<Image> mImages;
		
		public PageChangeListener(List<Image> images) {
			mImages = images;
		}

		@Override
		public void onItemChange(int currentPosition) {
			
            if (mImages != null) {
            	
            	Image Image = mImages.get(currentPosition);

            	mActionBar.setTitle((Image.getTitle() == null ? " "
                         : Image.getTitle().toLowerCase()));

            	mActionBar.setSubtitle(String.valueOf(currentPosition+1)
                        + "/" + String.valueOf(mImages.size()));

            	try {
            		String photoName = Image.getTitle();
					
				} catch (Exception e) {
					Crashlytics.logException(e);
				}
            }
		}
		
	}
	
	private void openGridView() {
		
		if(mSimpleGridViewSkipped) {
			toggleGridViews(false);
			mActionBar.setTitle(R.string.gallery);
			mActionBar.setSubtitle(String.valueOf(mProjectAllImagesList.size()) + " photos");
		} else {
			mActionBar.setTitle(mPrevTitle);
			mActionBar.setSubtitle(mPrevSubtitle);
		}
		
		mGridViewContainer.setVisibility(View.VISIBLE);
		mViewPager.setVisibility(View.INVISIBLE);
		
		if(mSimpleGridView.getVisibility() == View.VISIBLE) {
			mGalleryType = GalleryType.SimpleGridView;			
		} else {
			mGalleryType = GalleryType.CategorizedGridView;			
		}
		
	}
	
	private void openCategorizedGridView() {
		
		mGalleryType = GalleryType.CategorizedGridView;
		
		mActionBar.setTitle(R.string.gallery);
		mActionBar.setSubtitle(String.valueOf(mProjectAllImagesList.size()) + " photos");

		if(mCategorizedImageOverViewList.size() == 1) {
			openSimpleGridView(0);
			mCategorizedGridViewSkipped = true;
		} else {
			toggleGridViews(false);
			mCategorizedGridViewSkipped = false;
		}
	}
	
	private void openSimpleGridView(int position) {
		
		CategorizedImage cData = mCategorizedImageOverViewList.get(position);
		
		mGalleryType = GalleryType.SimpleGridView;

		toggleGridViews(true);
		
		mFullScrennImageList = cData.imageList;
		mCurrentCategoryName = getString(cData.titleId);
		
		SimpleGalleryThumbnailAdapter adapter = new SimpleGalleryThumbnailAdapter(mContext, mFullScrennImageList);
		adapter.setAnimationProperties(mSimpleGridView, position, mAnimationDuration);
		mSimpleGridView.setAdapter(adapter);

		mActionBar.setTitle(cData.titleId);
		mActionBar.setSubtitle(String.valueOf(cData.count) + " photos" );
		
		if(cData.imageList.size() == 1) {
			openFullScreenImageView(0); // skip simple grid view
			mSimpleGridViewSkipped = true;
		} else {
			mSimpleGridViewSkipped = false;
		}
		
	}
	
	private void openFullScreenImageView(int position) {
		
		mPrevTitle = mActionBar.getTitle();
		mPrevSubtitle = mActionBar.getSubtitle();

		mGalleryType = GalleryType.FullScreenImageView;
		mGridViewContainer.setVisibility(View.INVISIBLE);
		mViewPager.setVisibility(View.VISIBLE);
		
		List<Image> imageList = mFullScrennImageList;
		mUrlPagerAdapter = new UrlPagerAdapter(this, getFullScreenUrlList(imageList));
		mViewPager.setAdapter(mUrlPagerAdapter);
		mPageChangeListener = new PageChangeListener(imageList);
		mUrlPagerAdapter.setOnItemChangeListener(mPageChangeListener);
		mViewPager.setCurrentItem(position);
		
		mPageChangeListener.onItemChange(position);		
	}
	
	@Override
	public void onBackPressed() {		
		handleBackPressed();
	}

	private void handleBackPressed() {
		
		if(mDirectlyLaunchFullScreen) {
			super.onBackPressed();
			return;
		}
		
		switch (mGalleryType) {
		case CategorizedGridView:	
			super.onBackPressed();
			break;
		case SimpleGridView:
			if(mCategorizedGridViewSkipped) {
				super.onBackPressed();
			} else {
				openCategorizedGridView();
			}			
			break;
		case FullScreenImageView:
			if(mCategorizedGridViewSkipped && mSimpleGridViewSkipped) {
				super.onBackPressed();				
			} else {
				openGridView();
			}				
			break;

		default:
			super.onBackPressed();
			break;
		}
		
	}
	
	private void setProjectImages(List<Image> images) {

		for (Image Image : images) {
			if(Image.getImageType()==null || Image.getImageType().getType() == null){
				continue;
			}
			if (Image.getImageType().getType().equalsIgnoreCase(KeyUtil.CLUSTER_PLAN)) {
				mProjectImagesProjectPlansList.add(Image);
			} else if (Image.getImageType().getType().equalsIgnoreCase(KeyUtil.LOCATION_PLAN)) {
				mProjectImagesProjectPlansList.add(Image);
			} else if (Image.getImageType().getType().equalsIgnoreCase(KeyUtil.MASTER_PLAN)) {
				mProjectImagesProjectPlansList.add(Image);
			} else if (Image.getImageType().getType().equalsIgnoreCase(KeyUtil.SITE_PLAN)) {
				mProjectImagesProjectPlansList.add(Image);
			}else if (Image.getImageType().getType().equalsIgnoreCase(KeyUtil.LAYOUT_PLAN)) {
                mProjectImagesProjectPlansList.add(Image);
            }else if (Image.getImageType().getType().equalsIgnoreCase(KeyUtil.PAYMENT_PLAN)) {
				mProjectImagesPaymentPlanList.add(Image);
			} else if (Image.getImageType().getType().equalsIgnoreCase(KeyUtil.MAIN_PHOTO)) {
				mProjectImagesMainPhotoList.add(Image);
			} else if (Image.getImageType().getType().equalsIgnoreCase(KeyUtil.CONSTRUCTION_STATUS)) {
				mProjectImagesConstructionStatusList.add(Image);
			} else if (Image.getImageType().getType().equalsIgnoreCase(KeyUtil.FLOOR_PLAN)) {
				mProjectImagesFloorPlanList.add(Image);
			} else {
				mProjectImageGalleryList.add(Image);
			}
		}

		mProjectAllImagesList.addAll(mProjectImagesMainPhotoList);
		mProjectAllImagesList.addAll(mProjectImageGalleryList);
		mProjectAllImagesList.addAll(mProjectImagesProjectPlansList);
		mProjectAllImagesList.addAll(mProjectImagesFloorPlanList);
		mProjectAllImagesList.addAll(mProjectImagesPaymentPlanList);
		mProjectAllImagesList.addAll(mProjectImagesConstructionStatusList);
		mProjectAllImagesList.addAll(mProjectNeighbourhoodImageList);
		
		if(!mProjectAllImagesList.isEmpty()) {
			CategorizedImage cData = new CategorizedImage();
			cData.overViewImage = mProjectAllImagesList.get(0);
			cData.titleId = R.string.all_photos;
			cData.count = mProjectAllImagesList.size();
			cData.imageList = mProjectAllImagesList;
			mCategorizedImageOverViewList.add(cData);
		}
		
		if(!mProjectImagesProjectPlansList.isEmpty()) {
			CategorizedImage cData = new CategorizedImage();
			cData.overViewImage = mProjectImagesProjectPlansList.get(0);
			cData.titleId = R.string.tab_projectgallery_project_plan;
			cData.count = mProjectImagesProjectPlansList.size();
			cData.imageList = mProjectImagesProjectPlansList;
			mCategorizedImageOverViewList.add(cData);
		}
		
		if(!mProjectImagesFloorPlanList.isEmpty()) {
			CategorizedImage cData = new CategorizedImage();
			cData.overViewImage = mProjectImagesFloorPlanList.get(0);
			cData.titleId = R.string.tab_projectgallery_floor_plan;
			cData.count = mProjectImagesFloorPlanList.size();
			cData.imageList = mProjectImagesFloorPlanList;
			mCategorizedImageOverViewList.add(cData);
		}
		
		if(!mProjectImagesPaymentPlanList.isEmpty()) {
			CategorizedImage cData = new CategorizedImage();
			cData.overViewImage = mProjectImagesPaymentPlanList.get(0);
			cData.titleId = R.string.tab_projectgallery_payment_plan;
			cData.count = mProjectImagesPaymentPlanList.size();
			cData.imageList = mProjectImagesPaymentPlanList;
			mCategorizedImageOverViewList.add(cData);
		}
		
		if(!mProjectImagesConstructionStatusList.isEmpty()) {
			CategorizedImage cData = new CategorizedImage();
			cData.overViewImage = mProjectImagesConstructionStatusList.get(0);
			cData.titleId = R.string.tab_projectgallery_construction_update;
			cData.count = mProjectImagesConstructionStatusList.size();
			cData.imageList = mProjectImagesConstructionStatusList;
			mCategorizedImageOverViewList.add(cData);
		}
		
		if(!mProjectNeighbourhoodImageList.isEmpty()) {
			CategorizedImage cData = new CategorizedImage();
			cData.overViewImage = mProjectNeighbourhoodImageList.get(0);
			cData.titleId = R.string.tab_projectgallery_neighbourhood;
			cData.count = mProjectNeighbourhoodImageList.size();
			cData.imageList = mProjectNeighbourhoodImageList;
			mCategorizedImageOverViewList.add(cData);
		}		
		
		if(mCategorizedImageOverViewList.size() == 2 ) {
            try {
                if (mCategorizedImageOverViewList.get(0).count == mCategorizedImageOverViewList.get(1).count) {
                    // this means it has photos of only one category, so remove reduntant all photos
                    mCategorizedImageOverViewList.remove(0);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

		}
		
		mCategorizedGalleryThumbnailAdapter = new CategorizedGalleryThumbnailAdapter(mContext, mCategorizedImageOverViewList);
		mCategorizedGridView.setAdapter(mCategorizedGalleryThumbnailAdapter);
	}
	
	private String getImageBaseName(Image Image){
		return String.valueOf(Image.id);
	}
	
	public static class CategorizedImage {
		Image overViewImage;
		int titleId;
		Integer count;
		List<Image> imageList;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return true;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		mSimpleGridView.setSelection(mSimpleGridScrollPosition);
		
		int numOfColumns = ImageUtils.getNumColumns(mContext);
		
		mSimpleGridView.setNumColumns(numOfColumns);
		mCategorizedGridView.setNumColumns(numOfColumns);
	}
	
	private void toggleGridViews(boolean openSimpleGridView) {
		
		if(openSimpleGridView) {
			mSimpleGridView.setVisibility(View.VISIBLE);
			mCategorizedGridView.setVisibility(View.INVISIBLE);
		} else {	
			
			if(mSimpleGridViewSkipped) {
				mCategorizedGridView.setVisibility(View.VISIBLE);
				mSimpleGridView.setVisibility(View.INVISIBLE);
			} else {
				
				AlphaAnimation alpha = new AlphaAnimation(0, 1);
				alpha.setDuration(mAnimationDuration);
				mCategorizedGridView.startAnimation(alpha);			
				mCategorizedGridView.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						
						mSimpleGridView.setVisibility(View.INVISIBLE);
					}
				}, mAnimationDuration);
				
				
				SimpleGalleryThumbnailAdapter adapter = (SimpleGalleryThumbnailAdapter) mSimpleGridView.getAdapter();
				if(adapter != null) {
					adapter.animateBackToClickedCell();
				}
			}
		}		
	}
}
