package com.makaan.activity.listing;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.city.CityActivity;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.constants.ImageConstants;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.listing.OtherSellersGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.property.SimilarPropertyFragment;
import com.makaan.fragment.property.ViewSellersDialogFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SellerCard;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Project;
import com.makaan.response.property.Property;
import com.makaan.response.user.Company;
import com.makaan.response.user.User;
import com.makaan.service.ImageService;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.ui.CompressedTextView;
import com.makaan.ui.amenity.AmenityViewPager;
import com.makaan.ui.project.ProjectSpecificationView;
import com.makaan.ui.property.AboutBuilderExpandedLayout;
import com.makaan.ui.property.AmenitiesViewScroll;
import com.makaan.ui.property.FloorPlanLayout;
import com.makaan.ui.property.ListingDataOverViewScroll;
import com.makaan.ui.property.PropertyImageViewPager;
import com.makaan.ui.view.CustomRatingBar;
import com.makaan.ui.view.WishListButton;
import com.makaan.ui.view.WishListButton.WishListDto;
import com.makaan.ui.view.WishListButton.WishListType;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.makaan.util.RecentPropertyProjectManager;
import com.makaan.util.StringUtil;
import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 18/01/16.
 */
public class PropertyDetailFragment extends MakaanBaseFragment {

    @Bind(R.id.amenity_viewpager)
    AmenityViewPager mAmenityViewPager;

    @Bind(R.id.project_specification_view)
    ProjectSpecificationView projectSpecificationView;


    @Bind(R.id.property_image_viewpager)
    PropertyImageViewPager mPropertyImageViewPager;

    @Bind(R.id.floor_plan_layout)
    FloorPlanLayout mFloorPlanLayout;

    @Bind(R.id.serp_default_listing_property_shortlist_checkbox)
    public WishListButton mPropertyWishListCheckbox;

    @Bind(R.id.unit_name)
    TextView mUnitName;

    @Bind(R.id.unit_area)
    TextView mUnitArea;

    @Bind(R.id.seller_assist_button)
    Button mSellerAssistButton;

    @Bind(R.id.seller_rating)
    CustomRatingBar mSellerRating;

    @Bind(R.id.seller_image_view)
    CircularImageView mSellerImageView;

    @Bind(R.id.seller_logo_text_view)
    TextView mSellerLogoTextView;

    @Bind(R.id.seller_name_text_view)
    TextView mSellerName;

    @Bind(R.id.about_builder_layout)
    AboutBuilderExpandedLayout mABoutBuilderLayout;

    @Bind(R.id.about_locality)
    TextView mAboutLocality;

    @Bind(R.id.property_container)
    LinearLayout mPropertyContainer;

    @Bind(R.id.compressed_text_layout)
    CompressedTextView mCompressedDescriptionLayout;

    @Bind(R.id.listing_over_view_scroll_layout)
    ListingDataOverViewScroll mListingDataOverViewScroll;

    @Bind(R.id.amenities_scroll_layout)
    AmenitiesViewScroll mAmenitiesViewScroll;

    @Bind(R.id.locality_brief)
    TextView mLocalityBrief;

    @Bind(R.id.locality_score_progress)
    ProgressBar mLocalityScoreProgress;

    @Bind(R.id.all_seller_layout)
    LinearLayout mAllSellerLayout;

    @Bind(R.id.locality_score_text)
    TextView mLocalityScoreText;

    @Bind(R.id.content_text)
    TextView mListingBrief;

    @Bind(R.id.view_on_map)
    View mViewOnMap;


    @OnClick(R.id.more_about_locality)
    public void openLocality(){
        Intent intent = new Intent(getActivity(), LocalityActivity.class);
        intent.putExtra(KeyUtil.LOCALITY_ID,mListingDetail.property.project.localityId);
        startActivity(intent);
    }

    @OnClick(R.id.all_seller_text)
    public void openAllSellerDialog(){
        if(mSellerCards!=null) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ViewSellersDialogFragment viewSellersDialogFragment = new ViewSellersDialogFragment();
            viewSellersDialogFragment.bindView(mSellerCards);
            viewSellersDialogFragment.show(ft, "allSellers");
        }
    }

    @OnClick(R.id.contact_seller)
    public void openContactSeller(){
        if(mListingDetail != null &&
                mListingDetail.companySeller != null &&
                mListingDetail.companySeller.user !=null) {
            Intent intent = new Intent(getActivity(), LeadFormActivity.class);
            User user = mListingDetail.companySeller.user;
            Company company = mListingDetail.companySeller.company;
            try {
                intent.putExtra("name", company.name);
                if (company!=null && company.score != null) {
                    intent.putExtra("score", company.score.toString());
                } else {
                    intent.putExtra("score", "0");
                }
                if(user.contactNumbers!=null && !user.contactNumbers.isEmpty()) {
                    intent.putExtra("phone", user.contactNumbers.get(0).contactNumber);
                }
                intent.putExtra("id",user.id);
                getActivity().startActivity(intent);
            } catch (NullPointerException e) {
            }
        }
    }

    @Produce
    public ListingByIdGetEvent produceListing(){
        return new ListingByIdGetEvent(mListingDetail);
    }

    @OnClick(R.id.amenity_see_on_map)
    public void showMap(){
        mShowMapCallback.showMapFragment();
    }

    private ListingDetail mListingDetail;
    private List<AmenityCluster> mAmenityClusters = new ArrayList<>();
    private ShowMapCallBack mShowMapCallback;
    private Long listingId;
    private String listingMainUrl;
    private Context mContext;
    private ArrayList<ImagesGetEvent> mImagesGetEventArrayList;
    private ArrayList<SellerCard> mSellerCards;


    @Override
    protected int getContentViewId() {
        return R.layout.property_detail_frgament;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        Bundle args = getArguments();
        listingId = args.getLong(KeyUtil.LISTING_ID);
        listingMainUrl = args.getString(KeyUtil.LISTING_Image);
    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        if(amenityGetEvent.amenityClusters == null){
            return;
        }
        mAmenityViewPager.bindView();
        mAmenityClusters.clear();
        for(AmenityCluster cluster : amenityGetEvent.amenityClusters){
            if(null!=cluster && null!=cluster.cluster && cluster.cluster.size()>0){
                mAmenityClusters.add(cluster);
            }
        }

        if(!mAmenityClusters.isEmpty()){
            mAmenityViewPager.setVisibility(View.VISIBLE);
            mViewOnMap.setVisibility(View.VISIBLE);
            mAmenityViewPager.setData(mAmenityClusters);
        }
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent) {
        if(listingByIdGetEvent.listingDetail ==null){
            return;
        }
        else if(null != listingByIdGetEvent.error){
            getActivity().finish();
        }
        else {
            mListingDetail = listingByIdGetEvent.listingDetail;
            // adding property to recent manager
            RecentPropertyProjectManager manager = RecentPropertyProjectManager.getInstance(getActivity().getApplicationContext());
            manager.addEntryToRecent(manager.new DataObject(mListingDetail), getActivity().getApplicationContext());

            TestUi(mListingDetail);
            ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getOtherSellersOnListingDetail(
                    mListingDetail.projectId, mListingDetail.bedrooms, mListingDetail.bathrooms, mListingDetail.studyRoom
                    , mListingDetail.poojaRoom, mListingDetail.servantRoom, 5
            );
            ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(listingId);
            ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(listingId, ImageConstants.THREED_FLOOR_PLAN);
            ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(listingId, ImageConstants.FLOOR_PLAN);
        }
    }

    @Subscribe
    public void onResults(ImagesGetEvent imagesGetEvent){
        if(imagesGetEvent.imageType == null) {
            if (imagesGetEvent.images.size() > 0) {
                mPropertyImageViewPager.setVisibility(View.VISIBLE);
            }
            else{
                mPropertyImageViewPager.setVisibility(View.GONE);
            }
            mPropertyImageViewPager.bindView();
            mPropertyImageViewPager.setData(imagesGetEvent.images, mListingDetail.currentListingPrice.price, mListingDetail.currentListingPrice.pricePerUnitArea);
        }
        else if(imagesGetEvent.images!= null && imagesGetEvent.images.size()>0){
            mFloorPlanLayout.bindFloorPlan(imagesGetEvent);
        }
        else if(imagesGetEvent.error!=null){
            mFloorPlanLayout.setVisibility(View.GONE);
            mPropertyImageViewPager.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onOtherSellers(OtherSellersGetEvent otherSellersGetEvent){
        mSellerCards = otherSellersGetEvent.sellerCards;
    }

    private void TestUi(ListingDetail listingDetail){
        mPropertyContainer.setVisibility(View.VISIBLE);
        mListingDataOverViewScroll.bindView(listingDetail);
        // todo need to show seller logo image if available
        Company company = listingDetail.companySeller!=null?listingDetail.companySeller.company:null;
        if(company!=null) {
            showSellerDetail(company);
        }
        if(listingDetail.property != null) {
            Property property = listingDetail.property;

            mPropertyWishListCheckbox.bindView(new WishListDto(mListingDetail.id.longValue(),property.projectId.longValue(), WishListType.listing));
            if(listingDetail.listingAmenities !=null && !listingDetail.listingAmenities.isEmpty()) {
                mAmenitiesViewScroll.setVisibility(View.VISIBLE);
                mAmenitiesViewScroll.bindView(listingDetail.listingCategory,property.unitType, listingDetail.listingAmenities);
            }
            else{
                mAmenitiesViewScroll.setVisibility(View.GONE);
            }
            StringBuilder bhkInfo = new StringBuilder();
            if(property.unitType != null && "plot".equalsIgnoreCase(property.unitType)) {
                bhkInfo.append("residential plot");
            } else {
                bhkInfo.append(property.bedrooms > 0 ? property.bedrooms.toString()+"bhk ": "");
                bhkInfo.append(property.unitType != null ? property.unitType:"");
            }
            mUnitName.setText(bhkInfo.toString()+ " - ");

            if(getActivity() instanceof MakaanBaseSearchActivity) {
                getActivity().setTitle((listingDetail.property.bedrooms + "bhk " +
                        (property.unitType != null ? property.unitType : "")).toLowerCase());
            }

            mUnitArea.setText(
                    (property.size != null ? StringUtil.getFormattedNumber(property.size) : "") + " " +
                    (property.measure != null ? property.measure : ""));
            if(property.project != null) {
                Project project = property.project;
                if(project.getFormattedSpecifications() !=null && project.getFormattedSpecifications().size()>0) {
                    projectSpecificationView.setVisibility(View.VISIBLE);
                    projectSpecificationView.bindView(project.getFormattedSpecifications(), getActivity());
                }
                else {
                    projectSpecificationView.setVisibility(View.GONE);
                }

                mABoutBuilderLayout.bindView(listingDetail.property.project.builder);

                if(project.locality != null) {
                    Locality locality = project.locality;
                    mAboutLocality.setText("about ".concat((locality.label != null ? locality.label : "")));
                    mLocalityBrief.setText(Html.fromHtml((locality.description != null ? locality.description : "")));
                    if(locality.livabilityScore != null) {
                        mLocalityScoreProgress.setProgress((int) (locality.livabilityScore * 10));
                        mLocalityScoreText.setText(String.valueOf(locality.livabilityScore));
                    } else {
                        mLocalityScoreProgress.setVisibility(View.GONE);
                        mLocalityScoreText.setVisibility(View.GONE);
                    }
                }
                else{
                    mAboutLocality.setVisibility(View.GONE);
                    mLocalityScoreText.setVisibility(View.GONE);
                    mLocalityScoreProgress.setVisibility(View.GONE);
                }
            }
            if(listingDetail.description!=null) {
                mCompressedDescriptionLayout.setVisibility(View.VISIBLE);
                mListingBrief.setTextColor(getResources().getColor(R.color.listingBlack));
                mListingBrief.setText((listingDetail.description != null ? Html.fromHtml(listingDetail.description) : ""));
                ViewTreeObserver vto = mListingBrief.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(mListingBrief!=null) {
                            Layout l = mListingBrief.getLayout();
                            if (l != null) {
                                int lines = l.getLineCount();
                                if (lines > 0) {
                                    if (l.getEllipsisCount(lines - 1) > 0) {
                                        Log.d("test", "Text is ellipsized");
                                    } else {
                                        mCompressedDescriptionLayout.removeMore(true);
                                    }
                                }
                            }
                        }
                    }
                });
            }
            else{
                mCompressedDescriptionLayout.setVisibility(View.GONE);
            }
        }
    }

    private void showSellerDetail(Company company) {
        int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
        int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);
        mAllSellerLayout.setVisibility(View.VISIBLE);
        mSellerLogoTextView.setText(String.valueOf(company.name.charAt(0)));
        mSellerName.setText(String.format("%s(%s)", company.name, company.type));
        User user = mListingDetail.companySeller!=null?mListingDetail.companySeller.user:null;
        if(!company.assist){
            mSellerAssistButton.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(company.logo)) {
            mSellerLogoTextView.setVisibility(View.GONE);
            mSellerImageView.setVisibility(View.VISIBLE);
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(company.logo, width, height, false), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    showTextAsImage();
                }
            });
        } else if(user!=null && !TextUtils.isEmpty(user.profilePictureURL)) {
            mSellerLogoTextView.setVisibility(View.GONE);
            mSellerImageView.setVisibility(View.VISIBLE);
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(user.profilePictureURL, width, height, false), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    showTextAsImage();
                }
            });
        } else {
            showTextAsImage();
        }
    }

    private void showTextAsImage() {
        if(mListingDetail.companySeller.company.name == null) {
            mSellerLogoTextView.setVisibility(View.INVISIBLE);
        }
        mSellerLogoTextView.setText(String.valueOf(mListingDetail.companySeller.company.name.charAt(0)));
        mSellerLogoTextView.setVisibility(View.VISIBLE);
        mSellerImageView.setVisibility(View.GONE);
        // show seller first character as logo

        int[] bgColorArray = getResources().getIntArray(R.array.bg_colors);

        Random random = new Random();
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
//        int color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        drawable.getPaint().setColor(bgColorArray[random.nextInt(bgColorArray.length)]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSellerLogoTextView.setBackground(drawable);
        } else {
            mSellerLogoTextView.setBackgroundDrawable(drawable);
        }
    }

    //TODO add smiliar properties instead of this
    private void addSimilarProperties(List<ListingDetail> listingDetailList) {
        SimilarPropertyFragment newFragment = new SimilarPropertyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.similar_properties));
        newFragment.setArguments(bundle);
        initFragment(R.id.container_nearby_localities_props, newFragment, false);
        newFragment.setData(listingDetailList);
    }

    protected void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction =((CityActivity) mContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if (shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void bindView(ShowMapCallBack showMapCallBack) {
        mShowMapCallback = showMapCallBack;
    }
}
