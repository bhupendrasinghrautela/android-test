package com.makaan.ui.listing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.pojo.overview.OverviewItemType;
import com.makaan.response.listing.Listing;
import com.makaan.response.serp.ListingInfoMap;
import com.makaan.ui.CustomNetworkImageView;
import com.makaan.ui.view.WishListButton;
import com.makaan.ui.view.WishListButton.WishListDto;
import com.makaan.ui.view.WishListButton.WishListType;
import com.makaan.util.CommonUtil;
import com.makaan.util.DateUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.makaan.util.RecentPropertyProjectManager;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class DefaultListingView extends AbstractListingView {

    @Bind(R.id.serp_default_listing_property_shortlist_checkbox)
    public WishListButton mPropertyWishListCheckbox;

    @Bind(R.id.serp_default_listing_property_image_image_view)
    CustomNetworkImageView mPropertyImageView;
    @Bind(R.id.serp_default_listing_property_price_difference_image_view)
    ImageView mPropertyPriceDifferenceImageView;
    @Bind(R.id.serp_default_listing_badge_Image_view)
    ImageView mBadgeImageView;

    @Bind(R.id.serp_default_listing_property_possession_image_view)
    ImageView mPossessionImageView;
    @Bind(R.id.serp_default_listing_property_floor_image_view)
    ImageView mFloorImageView;
    @Bind(R.id.serp_default_listing_property_bathroom_image_view)
    ImageView mBathroomImageView;

    @Bind(R.id.serp_default_listing_seller_image_view)
    CircleImageView mSellerImageView;

    @Bind(R.id.serp_default_listing_seller_logo_text_view)
    TextView mSellerLogoTextView;

    @Bind(R.id.serp_default_listing_property_price_text_view)
    TextView mPropertyPriceTextView;
    @Bind(R.id.serp_default_listing_property_price_unit_text_view)
    TextView mPropertyPriceUnitTextView;
    @Bind(R.id.serp_default_listing_property_price_sq_ft_text_view)
    TextView mPropertyPriceSqFtTextView;
    @Bind(R.id.serp_default_listing_property_bhk_info_text_view)
    TextView mPropertyBhkInfoTextView;
    @Bind(R.id.serp_default_listing_property_size_info_text_view)
    TextView mPropertySizeInfoTextView;
    @Bind(R.id.serp_default_listing_property_address_text_view)
    TextView mPropertyAddressTextView;
    /*@Bind(R.id.serp_default_listing_property_tagline_text_view)
    TextView mPropertyDescriptionTextView;*/
    @Bind(R.id.serp_default_listing_seller_name_text_view)
    TextView mPropertySellerNameTextView;
    @Bind(R.id.serp_default_listing_badge_text_view)
    TextView mBadgeTextView;

    @Bind(R.id.serp_default_listing_property_possession_date_text_view)
    TextView mPropertyPossessionDateTextView;
    @Bind(R.id.serp_default_listing_property_floor_info_text_view)
    TextView mPropertyFloorInfoTextView;
    @Bind(R.id.serp_default_listing_property_bathroom_number_date_text_view)
    TextView mPropertyBathroomNumberTextView;

    @Bind(R.id.serp_default_listing_property_possession_text_view)
    TextView mPossesionTextView;
    @Bind(R.id.serp_default_listing_property_floor_text_view)
    TextView mFloorTextView;
    @Bind(R.id.serp_default_listing_property_bathroom_text_view)
    TextView mBathroomTextView;

    @Bind(R.id.serp_default_listing_assist_button)
    Button mAssistButton;
    @Bind(R.id.serp_default_listing_call_button)
    Button mCallButton;

    @Bind(R.id.serp_default_listing_seller_rating)
    RatingBar mSellerRatingBar;

    @Bind(R.id.serp_default_listing_property_seller_info_relative_layout)
    RelativeLayout mSellerInfoRelativeLayout;

    @Bind(R.id.serp_default_listing_empty_view)
    View mEmptyView;

    private SharedPreferences mPreferences;
    private Listing mListing;
    private SerpRequestCallback mCallback;

    ArrayList<ImageView> mPropertyInfoImageViews = new ArrayList<>();
    ArrayList<TextView> mPropertyInfoTextViews = new ArrayList<>();
    ArrayList<TextView> mPropertyInfoNameTextViews = new ArrayList<>();
    private int mPosition;

    public DefaultListingView(Context context) {
        super(context);
    }

    public DefaultListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback, int position) {
        super.populateData(data, callback, position);
        if(!(data instanceof Listing)) {
            return;
        }
        mPosition = position;

        mPropertyInfoImageViews.clear();
        mPropertyInfoTextViews.clear();
        mPropertyInfoNameTextViews.clear();

        mPropertyInfoImageViews.add(mPossessionImageView);
        mPropertyInfoImageViews.add(mFloorImageView);
        mPropertyInfoImageViews.add(mBathroomImageView);

        mPropertyInfoTextViews.add(mPropertyPossessionDateTextView);
        mPropertyInfoTextViews.add(mPropertyFloorInfoTextView);
        mPropertyInfoTextViews.add(mPropertyBathroomNumberTextView);

        mPropertyInfoNameTextViews.add(mPossesionTextView);
        mPropertyInfoNameTextViews.add(mFloorTextView);
        mPropertyInfoNameTextViews.add(mBathroomTextView);

        mCallback = callback;

        boolean isBuy = SerpObjects.isBuyContext(getContext());

        mListing = (Listing)data;

        if(RecentPropertyProjectManager.getInstance(mContext.getApplicationContext()).containsProperty(mListing.id)) {
            mBadgeImageView.setVisibility(View.VISIBLE);
            mBadgeTextView.setVisibility(View.VISIBLE);

            mBadgeImageView.setImageResource(R.drawable.badge_seen);
            mBadgeTextView.setText("seen");
        } else if(DateUtil.isNewListing(mListing.postedDate)) {
            mBadgeImageView.setVisibility(View.VISIBLE);
            mBadgeTextView.setVisibility(View.VISIBLE);

            mBadgeImageView.setImageResource(R.drawable.badge_new);
            mBadgeTextView.setText("new");
        } else {
            mBadgeImageView.setVisibility(View.GONE);
            mBadgeTextView.setVisibility(View.GONE);
        }

        WishListDto wishListDto=new WishListButton.WishListDto(mListing.lisitingId.longValue(), mListing.projectId.longValue(), WishListType.listing);
        wishListDto.setSerpItemPosition((long) mPosition);
        mPropertyWishListCheckbox.bindView(wishListDto);

        if(mListing.mainImageUrl != null && !TextUtils.isEmpty(mListing.mainImageUrl)) {
            int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_property_image_width);
            int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_property_image_height);
            mPropertyImageView.setDefaultImageResId(R.drawable.serp_placeholder);
            mPropertyImageView.setImageUrl(ImageUtils.getImageRequestUrl(mListing.mainImageUrl, width, height, false), MakaanNetworkClient.getInstance().getImageLoader());
        } else {
            // show placeholder
            Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap("serp_placeholder");
            if (bitmap == null) {
                int id = R.drawable.serp_placeholder;
                bitmap = BitmapFactory.decodeResource(getResources(), id);
                MakaanBuyerApplication.bitmapCache.putBitmap("serp_placeholder", bitmap);
            }

            mPropertyImageView.setImageBitmap(bitmap);
        }

        if(mListing.images != null && mListing.images.size() > 0) {

           /* ImageRequest request = new ImageRequest(listing.images.get(0).url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            mPropertyImageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            mPropertyImageView.setImageResource(R.drawable.temp_bulding);
                        }
                    });
            MakaanBuyerApplication.getInstance().addToRequestQueue(request, "");

            request = new ImageRequest(listing.images.get(0).url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            mSellerImageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            mSellerImageView.setImageResource(R.drawable.temp_builder);
                        }
                    });
            MakaanBuyerApplication.getInstance().addToRequestQueue(request, "");*/
        }

        if(mListing.lisitingPostedBy == null) {
            mSellerRatingBar.setVisibility(View.GONE);
        } else {
            mSellerRatingBar.setRating((float)mListing.lisitingPostedBy.rating);
        }
        mapPropertyInfo(isBuy);

        String priceString = StringUtil.getDisplayPrice(mListing.price);
        String priceUnit = "";
        if(priceString.indexOf("\u20B9") == 0) {
            priceString = priceString.replace("\u20B9", "");
        }
        String[] priceParts = priceString.split(" ");
        priceString = priceParts[0];
        if(priceParts.length > 1) {
            priceUnit = priceParts[1];
        }

        // set price info
        mPropertyPriceTextView.setText(priceString.toLowerCase());
        mPropertyPriceUnitTextView.setText(priceUnit);
        if(mListing.pricePerUnitArea != null && mListing.pricePerUnitArea != 0 && isBuy) {
            mPropertyPriceSqFtTextView.setVisibility(View.VISIBLE);
            mPropertyPriceSqFtTextView.setText(String.format("%s %s / sq ft", "\u20B9", StringUtil.getFormattedNumber(mListing.pricePerUnitArea)).toLowerCase());
        } else {
            mPropertyPriceSqFtTextView.setVisibility(View.INVISIBLE);
        }

        // set property bhk and size info
        if(mListing.bhkInfo == null) {
            mPropertyBhkInfoTextView.setVisibility(View.GONE);
        } else {
            mPropertyBhkInfoTextView.setVisibility(View.VISIBLE);
            mPropertyBhkInfoTextView.setText(mListing.bhkInfo.toLowerCase().replace("builderfloor","builder floor"));
        }

        if(mListing.sizeInfo == null || TextUtils.isEmpty(mListing.sizeInfo) || "0".equals(mListing.sizeInfo)) {
            mPropertySizeInfoTextView.setVisibility(View.GONE);
        } else {
            mPropertySizeInfoTextView.setVisibility(View.VISIBLE);
            mPropertySizeInfoTextView.setText(mListing.sizeInfo.toLowerCase());
        }

        // set property address info {project_name},{localityName}_{cityName}
        if(!TextUtils.isEmpty(mListing.project.name)
                && (mListing.project.activeStatus == null || !"dummy".equalsIgnoreCase(mListing.project.activeStatus))) {
            if(!TextUtils.isEmpty(mListing.project.builderName)) {
                mPropertyAddressTextView.setText(Html.fromHtml(String.format("<font color=\"#E71C28\">%s %s</font>, %s, %s", mListing.project.builderName,
                        mListing.project.name, mListing.localityName, mListing.cityName).toLowerCase()), TextView.BufferType.SPANNABLE);
            } else {
                mPropertyAddressTextView.setText(Html.fromHtml(String.format("<font color=\"#E71C28\">%s</font>, %s, %s", mListing.project.name, mListing.localityName,
                        mListing.cityName).toLowerCase()), TextView.BufferType.SPANNABLE);
            }
        } else {
            mPropertyAddressTextView.setText(String.format("%s, %s", mListing.localityName, mListing.cityName).toLowerCase());
        }

        // set property tagline or detailed info
        /*if(mListing.description != null) {
            String text = Html.fromHtml(mListing.description.toLowerCase()).toString();
            text = text.replace("\t", "").replace("\n", "");

            if("null".equalsIgnoreCase(text)) {
                mPropertyDescriptionTextView.setText("not available");
            } else {
                mPropertyDescriptionTextView.setText(text);
            }
        } else {
            mPropertyDescriptionTextView.setText("not available");
        }*/

        if(callback.needSellerInfoInSerp()) {
            mSellerInfoRelativeLayout.setVisibility(View.VISIBLE);

            if(mListing.lisitingPostedBy != null) {
                if("broker".equalsIgnoreCase(mListing.lisitingPostedBy.type)) {
                    mPropertySellerNameTextView.setText(Html.fromHtml(String.format("<font color=\"#E71C28\">%s</font> (%s)",
                            mListing.lisitingPostedBy.name, "agent").toLowerCase()), TextView.BufferType.SPANNABLE);
                } else {
                    mPropertySellerNameTextView.setText(Html.fromHtml(String.format("<font color=\"#E71C28\">%s</font> (%s)",
                            mListing.lisitingPostedBy.name, mListing.lisitingPostedBy.type).toLowerCase()), TextView.BufferType.SPANNABLE);
                }
            } else {
                mPropertySellerNameTextView.setText(mListing.project.builderName.toLowerCase());
            }

            int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
            int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);

            if(!TextUtils.isEmpty(mListing.lisitingPostedBy.logo)) {
                mSellerLogoTextView.setVisibility(View.GONE);
                mSellerImageView.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(mListing.lisitingPostedBy.logo, width, height, false),
                        new CustomImageLoaderListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        if(mSellerImageView!=null && mSellerLogoTextView!=null) {
                            mSellerLogoTextView.setVisibility(View.GONE);
                            mSellerImageView.setVisibility(View.VISIBLE);
                            mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        showTextAsImage();
                    }
                });
            } else if(!TextUtils.isEmpty(mListing.lisitingPostedBy.profilePictureURL)) {
                mSellerLogoTextView.setVisibility(View.GONE);
                mSellerImageView.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(mListing.lisitingPostedBy.profilePictureURL, width, height, false),
                        new CustomImageLoaderListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        if(mSellerImageView!=null && mSellerLogoTextView!=null) {
                            mSellerLogoTextView.setVisibility(View.GONE);
                            mSellerImageView.setVisibility(View.VISIBLE);
                            mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showTextAsImage();
                    }
                });
            } else {
                showTextAsImage();
            }

            if(mListing.lisitingPostedBy == null || !mListing.lisitingPostedBy.assist) {
                mAssistButton.setVisibility(View.GONE);
            } else {
                mAssistButton.setVisibility(View.VISIBLE);
            }
            //mAssistButton.setVisibility(View.VISIBLE);
        } else {
            mSellerInfoRelativeLayout.setVisibility(View.GONE);
        }

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListing != null) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    properties.put(MakaanEventPayload.LABEL, mListing.lisitingId + "_" + (mPosition + 1));
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickSerpPropertyView);

                    Bundle bundle = new Bundle();
                    bundle.putLong(OverviewActivity.ID, mListing.id);
                    bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROPERTY.ordinal());
                    if(mListing.latitude != null) {
                        bundle.putDouble(KeyUtil.LISTING_LAT, mListing.latitude);
                    }
                    if(mListing.longitude != null) {
                        bundle.putDouble(KeyUtil.LISTING_LON, mListing.longitude);
                    }
                    bundle.putString(KeyUtil.LISTING_Image, mListing.mainImageUrl);

                    mCallback.requestDetailPage(SerpActivity.REQUEST_PROPERTY_PAGE, bundle);
                }
            }
        });

    }

    private void showTextAsImage() {
        if(mListing.lisitingPostedBy == null || mListing.lisitingPostedBy.name == null
                || mListing.lisitingPostedBy.name.length() == 0) {
            mSellerLogoTextView.setVisibility(View.INVISIBLE);
            return;
        }
        mSellerLogoTextView.setText(String.valueOf(mListing.lisitingPostedBy.name.charAt(0)));
        mSellerLogoTextView.setVisibility(View.VISIBLE);
        mSellerImageView.setVisibility(View.GONE);
        // show seller first character as logo

//        int[] bgColorArray = getResources().getIntArray(R.array.bg_colors);

//        Random random = new Random();
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
//        int color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        drawable.getPaint().setColor(CommonUtil.getColor(mListing.lisitingPostedBy.name, getContext()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSellerLogoTextView.setBackground(drawable);
        } else {
            mSellerLogoTextView.setBackgroundDrawable(drawable);
        }
    }

    private void mapPropertyInfo(boolean isBuy) {
        // check possession for under construction properties
        // or age of property for reseller properties
        // or furnished/unfurnished/semi-furnished for rental properties
        if(isBuy) {
            if(!TextUtils.isEmpty(mListing.propertyType) && "Residential Plot".equalsIgnoreCase(mListing.propertyType)) {
                ArrayList<ListingInfoMap.InfoMap> map = MasterDataCache.getInstance().getListingMapInfo(ListingInfoMap.MAP_BUY_PLOT_PROPERTIES);
                mapPropertyInfo(map);
            } else {
                ArrayList<ListingInfoMap.InfoMap> map = MasterDataCache.getInstance().getListingMapInfo(ListingInfoMap.MAP_BUY_PROPERTIES);
                mapPropertyInfo(map);
                /*if (mListing.isReadyToMove && mListing.propertyAge != null && !TextUtils.isEmpty(mListing.propertyAge)) {
                    mPossesionTextView.setText("Age of Property");
                    mPropertyPossessionDateTextView.setText(mListing.propertyAge);
                } else if (mListing.possessionDate != null && !TextUtils.isEmpty(mListing.possessionDate)) {
                    mPossesionTextView.setText("Possession");
                    mPropertyPossessionDateTextView.setText(mListing.possessionDate);
                } else {
                    mPossesionTextView.setVisibility(View.GONE);
                    mPropertyPossessionDateTextView.setVisibility(View.GONE);
                    mPossessionImageView.setVisibility(View.GONE);
                }*/
            }
        } else {
            ArrayList<ListingInfoMap.InfoMap> map = MasterDataCache.getInstance().getListingMapInfo(ListingInfoMap.MAP_RENT_PROPERTIES);
            mapPropertyInfo(map);
            /*if(mListing.furnished != null && !TextUtils.isEmpty(mListing.furnished)) {
                mPossesionTextView.setText("Furnished");
                mPropertyPossessionDateTextView.setText(mListing.furnished);
            } else {
                mPossesionTextView.setVisibility(View.GONE);
                mPropertyPossessionDateTextView.setVisibility(View.GONE);
                mPossessionImageView.setVisibility(View.GONE);
            }*/
        }

        /*// set value of floor info of property out of total floors in building
        if(mListing.floor == 1) {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>st</sup> of %d", mListing.floor, mListing.totalFloors)));
        } else if(mListing.floor == 2) {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>nd</sup> of %d", mListing.floor, mListing.totalFloors)));
        } else if(mListing.floor == 3) {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>rd</sup> of %d", mListing.floor, mListing.totalFloors)));
        } else {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>th</sup> of %d", mListing.floor, mListing.totalFloors)));
        }

        // set bathroom info of property
        mPropertyBathroomNumberTextView.setText(String.valueOf(mListing.bathrooms));*/
    }

    private void mapPropertyInfo(ArrayList<ListingInfoMap.InfoMap> map) {
        int j = 0;
        for(int i = 0; i < map.size() && j < mPropertyInfoImageViews.size(); i++) {
            if(mapPropertyInfo(map.get(i), j)) {
                mPropertyInfoImageViews.get(j).setVisibility(View.VISIBLE);
                mPropertyInfoTextViews.get(j).setVisibility(View.VISIBLE);
                mPropertyInfoNameTextViews.get(j).setVisibility(View.VISIBLE);
                j++;
            }
        }
        if(j == 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        while(j < mPropertyInfoImageViews.size()) {
            mPropertyInfoImageViews.get(j).setVisibility(View.GONE);
            mPropertyInfoTextViews.get(j).setVisibility(View.GONE);
            mPropertyInfoNameTextViews.get(j).setVisibility(View.GONE);
            j++;
        }
    }

    private boolean mapPropertyInfo(ListingInfoMap.InfoMap infoMap, int j) {
        switch (infoMap.fieldName) {
            case "propertyStatus":
                if(!TextUtils.isEmpty(mListing.propertyStatus)) {
                    mPropertyInfoTextViews.get(j).setText(mListing.propertyStatus.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "propertyAge":
                if(mListing.isReadyToMove && mListing.age >= 0) {
                    if(mListing.age <= 1) {
                        mPropertyInfoTextViews.get(j).setText(String.format("%d - %d yr", mListing.age, mListing.age + 1));
                    } else if(mListing.age <= 2) {
                        mPropertyInfoTextViews.get(j).setText(String.format("%d - %d yrs", mListing.age, mListing.age + 1));
                    } else if(mListing.age <= 5) {
                        mPropertyInfoTextViews.get(j).setText(String.format("%d - %d yrs", 2, 5));
                    } else {
                        mPropertyInfoTextViews.get(j).setText(String.format(">%d yrs", 5));
                    }
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "possessionDate":
                if(!mListing.isReadyToMove && !TextUtils.isEmpty(mListing.possessionDate)) {
                    mPropertyInfoTextViews.get(j).setText(mListing.possessionDate.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "bathrooms":
                if(mListing.bathrooms != null && mListing.bathrooms > 0) {
                    mPropertyInfoTextViews.get(j).setText(String.valueOf(mListing.bathrooms).toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "floor,totalFloors":
                if(mListing.floor != null && mListing.totalFloors != null && mListing.floor >= 0 && mListing.totalFloors != 0) {
                    if(mListing.floor == 1) {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup><small>st</small></sup> of %d", mListing.floor, mListing.totalFloors).toLowerCase()));
                    } else if(mListing.floor == 2) {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup><small>nd</small></sup> of %d", mListing.floor, mListing.totalFloors).toLowerCase()));
                    } else if(mListing.floor == 3) {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup><small>rd</small></sup> of %d", mListing.floor, mListing.totalFloors).toLowerCase()));
                    } else if(mListing.floor == 0) {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%s of %d", "gr", mListing.totalFloors).toLowerCase()));
                    } else {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup><small>th</small></sup> of %d", mListing.floor, mListing.totalFloors).toLowerCase()));
                    }
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else if(mListing.floor != null && mListing.floor >= 0) {
                    if(mListing.floor == 1) {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup><small>st</small></sup>", mListing.floor).toLowerCase()));
                    } else if(mListing.floor == 2) {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup><small>nd</small></sup>", mListing.floor).toLowerCase()));
                    } else if(mListing.floor == 3) {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup><small>rd</small></sup> ", mListing.floor).toLowerCase()));
                    } else if(mListing.floor == 0) {
                        mPropertyInfoTextViews.get(j).setText("gr");
                    } else {
                        mPropertyInfoTextViews.get(j).setText(Html.fromHtml(String.format("%d<sup><small>th</small></sup>", mListing.floor).toLowerCase()));
                    }
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "balcony":
                if(mListing.balcony != null && mListing.balcony > 0) {
                    mPropertyInfoTextViews.get(j).setText(String.valueOf(mListing.balcony).toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "listingCategory":
                if(!TextUtils.isEmpty(mListing.listingCategory)) {
                    if("primary".equalsIgnoreCase(mListing.listingCategory)) {
                        mPropertyInfoTextViews.get(j).setText("new".toLowerCase());
                    } else if("resale".equalsIgnoreCase(mListing.listingCategory)) {
                        mPropertyInfoTextViews.get(j).setText("resale".toLowerCase());
                    } else {
                        mPropertyInfoTextViews.get(j).setText(mListing.listingCategory.toLowerCase());
                    }
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "facing":
                if(!TextUtils.isEmpty(mListing.facing)) {
                    mPropertyInfoTextViews.get(j).setText(mListing.facing.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "ownershipType":
                if(!TextUtils.isEmpty(mListing.ownershipType)) {
                    mPropertyInfoTextViews.get(j).setText(mListing.ownershipType.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "furnished":
                if(!TextUtils.isEmpty(mListing.furnished)) {
                    mPropertyInfoTextViews.get(j).setText(mListing.furnished.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "isReadyToMove":
                if(!mListing.isReadyToMove && !TextUtils.isEmpty(mListing.possessionDate)) {
                    mPropertyInfoTextViews.get(j).setText(mListing.possessionDate.toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else if(mListing.isReadyToMove) {
                    mPropertyInfoTextViews.get(j).setText("ready to move in");
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "noOfOpenSides":
                if(mListing.noOfOpenSides != null && mListing.noOfOpenSides > 0) {
                    mPropertyInfoTextViews.get(j).setText(String.valueOf(mListing.noOfOpenSides).toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            case "securityDeposit":
                if(mListing.securityDeposit != null && mListing.securityDeposit > 0) {
                    mPropertyInfoTextViews.get(j).setText(String.valueOf(mListing.securityDeposit).toLowerCase());
                    mPropertyInfoNameTextViews.get(j).setText(infoMap.displayName.toLowerCase());
                    break;
                } else {
                    return false;
                }
            default:
                return false;
        }
        if(infoMap.imageName != null) {
            Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap(infoMap.imageName);
            if (bitmap != null) {
                mPropertyInfoImageViews.get(j).setImageBitmap(bitmap);
            } else {
                int id = this.getResources().getIdentifier(infoMap.imageName, "drawable", "com.makaan");
                Bitmap b = BitmapFactory.decodeResource(getResources(), id);
                mPropertyInfoImageViews.get(j).setImageBitmap(b);
                MakaanBuyerApplication.bitmapCache.putBitmap(infoMap.imageName, b);
            }
        }
        return true;
    }

    @OnClick({R.id.serp_default_listing_seller_image_frame_layout, R.id.serp_default_listing_seller_name_text_view, R.id.serp_default_listing_seller_rating})
    public void onSellerPressed(View view) {
        // TODO discuss what should be done if listing posted by is not present
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
        properties.put(MakaanEventPayload.LABEL, mListing.lisitingId + "_" + (mPosition + 1)+"_" + mListing.lisitingPostedBy.id);
        MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickSerpPropertySeller);
        
        SerpRequest request = new SerpRequest(SerpActivity.TYPE_SELLER);
        request.setSellerId(mListing.lisitingPostedBy.id);
        request.setTitle(mListing.lisitingPostedBy.name);
        request.launchSerp(getContext());

//        MakaanBuyerApplication.mSerpSelector.term("sellerId", String.valueOf(mListing.sellerId));
    }

    @OnClick(R.id.serp_default_listing_property_address_frame_layout)
    public void onProjectClicked(View view) {
        if(!TextUtils.isEmpty(mListing.project.name)
                && (mListing.project.activeStatus == null || !"dummy".equalsIgnoreCase(mListing.project.activeStatus))) {
            if (mListing.projectId != null && mListing.projectId != 0) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                properties.put(MakaanEventPayload.LABEL, mListing.lisitingId + "_" + (mPosition + 1)+"_" + mListing.projectId);
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickProject);

                Bundle bundle = new Bundle();
                bundle.putLong(OverviewActivity.ID, mListing.projectId);
                bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROJECT.ordinal());
                mCallback.requestDetailPage(SerpActivity.REQUEST_PROJECT_PAGE, bundle);
            }
        }
    }

    @OnClick(R.id.serp_default_listing_call_button)
    public void onCallClicked(View view) {
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
        properties.put(MakaanEventPayload.LABEL, mListing.lisitingId + "_" + (mPosition + 1));
        MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.callSerpProperty);

        Bundle bundle = new Bundle();

        bundle.putString(KeyUtil.NAME_LEAD_FORM, mListing.lisitingPostedBy.name);
        bundle.putString(KeyUtil.SCORE_LEAD_FORM, String.valueOf(mListing.lisitingPostedBy.rating));

        if(mListing.lisitingPostedBy!=null && mListing.lisitingPostedBy.number!=null) {
            bundle.putString(KeyUtil.PHONE_LEAD_FORM, mListing.lisitingPostedBy.number);
        }
        bundle.putString(KeyUtil.SINGLE_SELLER_ID, String.valueOf(mListing.lisitingPostedBy.id));
        bundle.putLong(KeyUtil.LISTING_ID_LEAD_FORM, mListing.lisitingId);

        if(mListing!=null && mListing.cityName!=null && !TextUtils.isEmpty(mListing.listingCategory)){
            if(mListing.listingCategory.equalsIgnoreCase("primary")||mListing.listingCategory.equalsIgnoreCase("resale")){
                bundle.putString(KeyUtil.SALE_TYPE_LEAD_FORM, "buy");
            }
            else if(mListing.listingCategory.equalsIgnoreCase("rental")){
                bundle.putString(KeyUtil.SALE_TYPE_LEAD_FORM, "rent");
            }
        }

        if(mListing!=null){
            if( mListing.cityName!=null && !TextUtils.isEmpty(mListing.cityName)){
                bundle.putString(KeyUtil.CITY_NAME_LEAD_FORM, mListing.cityName);
            }
            if(mListing.cityId!=null){
                bundle.putLong(KeyUtil.CITY_ID_LEAD_FORM, mListing.cityId);
            }
        }

        if(mListing != null && mListing.projectId != null && mListing.projectId > 0) {
            bundle.putLong(KeyUtil.PROJECT_ID_LEAD_FORM, mListing.projectId);
        } else if(mListing!=null && mListing.project!=null && mListing.project.projectId!=null){
            bundle.putLong(KeyUtil.PROJECT_ID_LEAD_FORM, mListing.project.projectId);
        }

        if(mListing!=null && mListing.project!=null && mListing.project.name!=null){
            bundle.putString(KeyUtil.PROJECT_NAME_LEAD_FORM, mListing.project.name);
        }

        if(mListing != null && mListing.localityId != null && mListing.localityId > 0) {
            bundle.putLong(KeyUtil.LOCALITY_ID_LEAD_FORM, mListing.localityId);
        } else if(mListing!=null && mListing.project!=null && mListing.project.locality!=null && mListing.project.locality.localityId!=null) {
            bundle.putLong(KeyUtil.LOCALITY_ID_LEAD_FORM, mListing.project.locality.localityId);
        }

        if(!TextUtils.isEmpty(mListing.lisitingPostedBy.logo)) {
            bundle.putString(KeyUtil.SELLER_IMAGE_URL_LEAD_FORM,mListing.lisitingPostedBy.logo);
        }
        else if(!TextUtils.isEmpty(mListing.lisitingPostedBy.profilePictureURL)) {
            bundle.putString(KeyUtil.SELLER_IMAGE_URL_LEAD_FORM, mListing.lisitingPostedBy.profilePictureURL);
        }

        bundle.putString(KeyUtil.SOURCE_LEAD_FORM, SerpActivity.class.getName());

        mCallback.requestDetailPage(SerpActivity.REQUEST_LEAD_FORM, bundle);
    }

    @OnClick(R.id.serp_default_listing_assist_button)
    public void onAssistClicked(View view) {
        if(mCallback != null) {
            mCallback.requestDetailPage(SerpActivity.REQUEST_MPLUS_POPUP, null);
        }
    }

}
